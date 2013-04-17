Ext.define('Admin.view.WizardHeader', {
    extend: 'Ext.form.Panel',
    alias: 'widget.wizardHeader',

    cls: 'admin-wizard-header-container',

    border: false,

    displayNameProperty: 'displayName',
    displayNameConfig: {
        emptyText: 'Display Name',
        enableKeyEvents: true,
        hideLabel: true,
        autoFocus: true
        // TODO: What is max length?
        //maxLength: 255,
        //enforceMaxLength: true
    },

    pathProperty: 'path',
    pathConfig: {
        hidden: false,
        emptyText: 'path/to/',
        hideLabel: true
    },

    nameProperty: 'name',

    nameConfig: {
        hidden: false,
        allowBlank: false,
        emptyText: 'Name',
        enableKeyEvents: true,
        hideLabel: true,
        vtype: 'name',
        stripCharsRe: /[^a-z0-9\-]+/ig
        // TODO: What is max length?
        //maxLength: 255,
        //enforceMaxLength: true
    },


    initComponent: function () {
        var me = this;

        me.appendVtypes();

        var headerData = this.prepareHeaderData(this.data);

        me.autogenerateName = Ext.isEmpty(headerData[this.nameProperty]);
        me.autogenerateDisplayName = Ext.isEmpty(headerData[this.displayNameProperty]);

        this.displayNameField = Ext.create('Ext.form.field.Text', Ext.apply({
            xtype: 'textfield',
            grow: true,
            growMin: 200,
            name: this.displayNameProperty,
            value: headerData[this.displayNameProperty],
            cls: 'admin-display-name',
            dirtyCls: 'admin-display-name-dirty'
        }, me.displayNameConfig, Admin.view.WizardHeader.prototype.displayNameConfig));

        // add listeners separately so they don't get overridden by config
        this.displayNameField.on({
            afterrender: me.onDisplayNameAfterrender,
            keyup: me.onDisplayNameKey,
            change: me.onDisplayNameChanged,
            scope: me
        });

        this.pathField = Ext.create('Ext.form.field.Display', Ext.apply({
            xtype: 'displayfield',
            cls: 'admin-path',
            dirtyCls: 'admin-path-dirty',
            value: headerData[this.pathProperty]
        }, me.pathConfig, Admin.view.WizardHeader.prototype.pathConfig));

        this.nameField = Ext.create('Ext.form.field.Text', Ext.apply({
            xtype: 'textfield',
            grow: true,
            growMin: 60,
            cls: 'admin-name',
            dirtyCls: 'admin-name-dirty',
            name: this.nameProperty,
            value: headerData[this.nameProperty],
            listeners: {
                change: function (textfield, newValue) {
                    textfield.setValue(textfield.processRawValue(newValue));
                }, scope: this
            }

        }, me.nameConfig, Admin.view.WizardHeader.prototype.nameConfig));

        // add listeners separately so they don't get overridden by config
        this.nameField.on({
            keyup: me.onNameKey,
            change: me.onNameChanged,
            scope: me
        });

        this.items = [
            me.displayNameField
        ];

        if (!me.pathField.hidden && !me.nameField.hidden) {
            this.items.push({
                xtype: 'fieldcontainer',
                hideLabel: true,
                layout: 'hbox',
                items: [
                    me.pathField,
                    me.nameField
                ]
            });
        } else if (!me.pathField.hidden) {
            this.items.push(me.pathField);
        } else if (!me.nameField.hidden) {
            this.items.push(me.nameField);
        }

        this.callParent(arguments);
        this.addEvents('displaynamechange', 'displaynameoverride', 'namechange', 'nameoverride');
    },

    onDisplayNameAfterrender: function (field) {
        if (!field.readOnly && field.autoFocus) {
            field.getFocusEl().focus(100);
        }
    },

    onDisplayNameKey: function (field, event, opts) {
        var wasAutoGenerate = this.autogenerateDisplayName;
        var autoGenerate = Ext.isEmpty(field.getValue());
        if (wasAutoGenerate != autoGenerate) {
            this.fireEvent('displaynameoverride', !autoGenerate);
        }
        this.autogenerateDisplayName = autoGenerate;
    },

    onDisplayNameChanged: function (field, newVal, oldVal, opts) {
        if (this.fireEvent('displaynamechange', newVal, oldVal) !== false && this.autogenerateName) {
            var processedValue = this.nameField.processRawValue(this.preProcessName(newVal));
            this.nameField.setValue(processedValue);
        }

        this.nameField.growMax = this.el.getWidth() - 100;
        this.nameField.doComponentLayout();
    },

    onNameKey: function (field, event, opts) {
        var wasAutoGenerate = this.autogenerateName;
        var autoGenerate = Ext.isEmpty(field.getValue());
        if (wasAutoGenerate != autoGenerate) {
            this.fireEvent('nameoverride', !autoGenerate);
        }
        this.autogenerateName = autoGenerate;
    },

    onNameChanged: function (field, newVal, oldVal, opts) {
        this.fireEvent('namechange', newVal, oldVal);
    },

    appendVtypes: function () {
        Ext.apply(Ext.form.field.VTypes, {
            name: function (val, field) {
                return /^[a-z0-9\-]+$/i.test(val);
            },
            nameText: 'Not a valid name. Can contain digits, letters and "-" only.',
            nameMask: /^[a-z0-9\-]+$/i
        });
        Ext.apply(Ext.form.field.VTypes, {
            qualifiedName: function (val, field) {
                return /^[a-z0-9\-:]+$/i.test(val);
            },
            qualifiedNameText: 'Not a valid qualified name. Can contain digits, letters, ":" and "-" only.',
            qualifiedNameMask: /^[a-z0-9\-:]+$/i
        });
        Ext.apply(Ext.form.field.VTypes, {
            path: function (val, field) {
                return /^[a-z0-9\-\/]+$/i.test(val);
            },
            pathText: 'Not a valid path. Can contain digits, letters, "/" and "-" only.',
            pathMask: /^[a-z0-9\-\/]+$/i
        });
    },

    preProcessName: function (displayName) {
        return !Ext.isEmpty(displayName) ? displayName.replace(/[\s+\./]/ig, '-')
            .replace(/-{2,}/g, '-')
            .replace(/^-|-$/g, '')
            .toLowerCase() : '';
    },

    prepareHeaderData: function (data) {
        return data && data.data || data || {};
    },

    setData: function (data) {
        this.data = data;
        this.getForm().setValues(this.resolveHeaderData(data));
    },

    getData: function () {
        return this.getForm().getFieldValues();
    },

    getDisplayName: function () {
        return this.displayNameField.getValue();
    },

    setDisplayName: function (displayName) {
        this.displayNameField.setValue(displayName);
    }

});