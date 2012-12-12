Ext.define('Admin.view.TopBar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.topBar',

    requires: [
        'Admin.view.TopBarMenu',
        'Admin.view.StartMenu',
        'Admin.view.AdminImageButton'
    ],

    buttonAlign: 'center',
    cls: 'admin-topbar-panel',

    dock: 'top',
    plain: true,
    border: false,

    initComponent: function () {
        var me = this;

        this.tabMenu = Ext.create('Admin.view.TopBarMenu', {
            tabPanel: me.tabPanel,
            listeners: {
                close: {
                    fn: me.syncTabCount,
                    scope: me
                },
                closeAll: {
                    fn: me.syncTabCount,
                    scope: me
                }
            }
        });

        this.tabButton = Ext.create('Ext.button.Button', {
            cls: 'white',
            menuAlign: 't-b?',
            menu: me.tabMenu
        });

        this.startMenu = Ext.create('Admin.view.StartMenu', {
            xtype: 'startMenu',
            renderTo: Ext.getBody(),
            listeners: {
                login: function (cmp) {
                    cmp.loggedUser = {
                        img: '../html-templates/images/profile-image.png'
                    };
                },
                logout: function (cmp) {
                    delete cmp.loggedUser;
                }
            },
            items: [
                {
                    id: 'app-01',
                    title: 'Content Manager',
                    cls: 'span2 start-row content-manager',
                    iconCls: 'icon-content-manager-24',
                    appUrl: 'app-content-manager.jsp'
                },
                {
                    id: 'app-02',
                    title: 'Dashboard',
                    cls: 'span2 dashboard',
                    iconCls: 'icon-dashboard-24',
                    appUrl: 'app-dashboard.jsp',
                    defaultApp: true
                },
                {
                    id: 'app-03',
                    title: 'Profile',
                    cls: 'profile title-top title-mask',
                    img: '../dev/html-templates/images/profile-image.png',
                    appUrl: 'blank.html',
                    contentTpl: new Ext.XTemplate('<img src="{img}" alt=""/>')
                },
                {
                    id: 'app-04',
                    title: 'Activity Stream',
                    cls: 'span2 activity title-top title-right',
                    appUrl: 'blank.html',
                    posts: [
                        {
                            img: '../dev/html-templates/images/profile-image.png',
                            text: 'Hello there !'
                        },
                        {
                            img: '../dev/html-templates/images/profile-image.png',
                            text: 'What do you think about this new tile based start menu ?'
                        },
                        {
                            img: '../dev/html-templates/images/profile-image.png',
                            text: 'Huh ?'
                        }
                    ],
                    contentTpl: new Ext.XTemplate('<tpl for="posts"><div class="item">' +
                                                  '<img src="{img}"/><p>{text}</p></div></tpl>')
                },
                {
                    id: 'app-05',
                    title: 'Cluster',
                    appUrl: 'blank.html',
                    iconCls: "icon-cluster-24",
                    cls: 'cluster'
                },
                {
                    id: 'app-06',
                    title: 'Userstores',
                    appUrl: 'app-userstore.jsp',
                    iconCls: "icon-userstore-alt-24",
                    cls: 'userstores start-row'
                },
                {
                    id: 'app-07',
                    title: 'Accounts',
                    appUrl: 'app-account.jsp',
                    iconCls: "icon-woman-24",
                    cls: 'accounts'
                },
                {
                    id: 'app-08',
                    title: 'Site Administrator',
                    appUrl: 'blank.html',
                    cls: 'span2 administrators'
                },
                {
                    id: 'app-09',
                    title: 'Content Studio',
                    appUrl: 'app-content-studio.jsp',
                    iconCls: 'icon-content-studio-24',
                    cls: 'content-studio'
                },
                {
                    id: 'app-10',
                    title: 'Live Trace',
                    appUrl: 'blank.html',
                    iconCls: "icon-live-trace-24",
                    cls: 'span2 live-trace title-top title-mask'
                },
                {
                    id: 'app-11',
                    title: 'Schedule',
                    appUrl: 'blank.html',
                    iconCls: "icon-gears-run-24",
                    cls: 'schedule'
                }
            ]
        });

        this.startButton = Ext.create('Ext.button.Button', {
            xtype: 'button',
            itemId: 'app-launcher-button',
            cls: 'start-button',
            iconCls: me.appIconCls,
            text: me.appName || '&lt; app name &gt;',
            handler: function (btn, evt) {
                me.startMenu.slideToggle();
            }
        });

        this.path = Ext.create('Ext.Component');

        this.items = [
            me.startButton,
            me.tabButton,
            { xtype: 'tbspacer', flex: 5 },
            me.path,
            { xtype: 'tbspacer', flex: 5 },
            {
                xtype: 'adminImageButton',
                icon: "rest/account/image/default/user",
                popupTpl: Templates.common.userPopUp,
                popupData: {
                    userName: "Thomas Lund Sigdestad",
                    photoUrl: "rest/account/image/default/user",
                    qName: 'system/tsi'
                }
            }
        ];

        this.callParent(arguments);

        this.syncTabCount();
    },


    /*  Methods for Admin.view.TabPanel integration */

    insert: function (index, cfg) {

        var added = this.tabMenu.addItems(cfg);
        this.syncTabCount();

        return added.length === 1 ? added[0] : undefined;
    },

    setActiveTab: function (tab) {

        if (tab.card.id !== 'tab-browse') {
            this.setPath(this.getMenuItemDescription(tab.card));
        } else {
            this.setPath('');
        }
    },

    remove: function (tab) {

        var removed = this.tabMenu.removeItems(tab);
        this.syncTabCount();

        return removed;
    },

    findNextActivatable: function (tab) {

        if (this.tabPanel) {
            // set first browse tab active
            return this.tabPanel.items.get(0);
        }
    },

    createMenuItemFromTab: function (item) {
        var me = this;
        var cfg = item.initialConfig || item;
        var data = item.data || item;
        return {
            tabBar: me,
            card: item,
            disabled: cfg.disabled,
            closable: cfg.closable,
            hidden: cfg.hidden && !item.hiddenByLayout, // only hide if it wasn't hidden by the layout itself
            iconCls: cfg.iconCls,
            iconSrc: data.image_url,
            editing: cfg.editing || false,
            text1: cfg.title || 'first line',
            text2: me.getMenuItemDescription(item)
        };
    },

    /*  Private */

    syncTabCount: function () {
        var count = this.tabMenu.getAllItems().length;
        // show counter when 2 or more tabs are open
        this.tabButton.setVisible(count > 1);
        this.tabButton.setText('' + count);
    },

    getMenuItemDescription: function (card) {
        return card.data ? card.data.path || card.data.qualifiedName || card.data.displayName : card.title
    },

    /*  Public  */

    getPath: function () {
        return this.path.getBody().el.getHTML();
    },

    setPath: function (path) {
        this.path.update(path);
    },

    getStartButton: function () {
        return this.startButton;
    },

    getStartMenu: function () {
        return this.startMenu;
    },

    getTabButton: function () {
        return this.tabButton;
    },

    getTabMenu: function () {
        return this.tabMenu;
    }
});
