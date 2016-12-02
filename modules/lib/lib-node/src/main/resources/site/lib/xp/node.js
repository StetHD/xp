/**
 * Functions to get, query and manipulate nodes.
 *
 * @example
 * var nodeLib = require('/lib/xp/node');
 *
 * @module lib/xp/node
 */


var factory = __.newBean('com.enonic.xp.lib.node.NodeHandleFactory');

function required(params, name) {
    var value = params[name];
    if (value === undefined) {
        throw "Parameter '" + name + "' is required";
    }
    return value;
}

function nullOrValue(value) {
    if (value === undefined) {
        return null;
    }
    return value;
}

function valueOrDefault(value, defaultValue) {
    if (value === undefined) {
        return defaultValue;
    }
    return value;
}

/**
 * Creates a new repo-connection.
 *
 * @param {*} native Native repo-connection object.
 * @constructor
 * @private
 */
function RepoConnection(native) {
    this.native = native;
}

/**
 * This function creates a node.
 *
 *
 * To create a content where the name is not important and there could be multiple instances under the same parent content,
 * skip the `name` parameter and specify a `displayName`.
 *
 * @example-ref examples/node/create-1.js
 * @example-ref examples/node/create-2.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} [params._name] Name of content.
 * @param {string} [params._parentPath] Path to place content under.
 * @param {object} [params._indexConfig] How the document should be indexed. A default value "byType" will be set if no value specified.
 * @param {object} [params._permissions] The access control list for the node. By default the creator will have full access
 * @param {number} [params._manualOrderValue] Value used to order document when ordering by parent and child-order is set to manual
 * @param {string} [params._childOrder] Default ordering of children when doing getChildren if no order is given in query
 *
 * @returns {object} Node created as JSON.
 */
RepoConnection.prototype.create = function (params) {
    var scriptValue = __.toScriptValue(params);
    return __.toNativeObject(this.native.create(scriptValue));
};


/**
 * This function modifies a node.
 *
 * @example-ref examples/node/modify.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the node.
 * @param {function} params.editor Editor callback function.
 *
 * @returns {object} Modified node as JSON.
 */
RepoConnection.prototype.modify = function (params) {
    var editor = __.toScriptValue(params.editor);
    var key = required(params, 'key');
    return __.toNativeObject(this.native.modify(editor, key));
};

/**
 * This function fetches nodes. If key is defined, the fetched node will be returned as a JSON object or null if not found.
 * If keys is defined, the fetched nodes will be return as a JSON array.
 *
 * @example-ref examples/node/get.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} [params.key] Path or id to the node.
 * @param {string[]} [params.keys] Path or id array to the nodes.
 *
 * @returns {object} The node or node array (as JSON) fetched from the repository.
 */
RepoConnection.prototype.get = function (params) {
    if (params.key === undefined && params.keys === undefined) {
        throw "Parameter 'key' or 'keys' is required";
    }
    var key = params.key ? params.key : null;
    var keys = params.keys ? params.keys : [];
    return __.toNativeObject(this.native.get(key, keys));
};


/**
 * This function deletes a node.
 *
 * @example-ref examples/node/delete.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key Path or id to the node.
 * @param {string[]} [params.keys] Path or id array to the nodes.
 *
 * @returns {boolean} True if deleted, false otherwise.
 */
RepoConnection.prototype.delete = function (params) {
    if (params.key === undefined && params.keys === undefined) {
        throw "Parameter 'key' or 'keys' is required";
    }
    var key = params.key ? params.key : null;
    var keys = params.keys ? params.keys : [];
    return __.toNativeObject(this.native.delete(key, keys));
};


/**
 * This function push a node to a given branch.
 *
 * @example-ref examples/node/push.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string[]} params.keys ids array to the nodes.
 * @param {string} params.target Branch to push nodes to.
 * @param {boolean} params.resolve resolve dependencies before pushing
 * @param {boolean} params.resolve.includeChildren include children of given ids for pushing
 * @param {string[]} params.resolve.exclude exclude these nodes if not needed to maintain data integrity (e.g parents must be present in target)
 *
 * @returns {object} PushNodesResult
 */
RepoConnection.prototype.push = function (params) {
    var handlerParams = __.newBean('com.enonic.xp.lib.node.PushNodeHandlerParams');
    params = params || {};
    handlerParams.keys = required(params, 'keys');
    handlerParams.targetBranch = required(params, 'target');
    if (params.resolve) {
        handlerParams.resolve = params.resolve;
        handlerParams.includeChildren = valueOrDefault(params.resolve.includeChildren, true);
        if (params.resolve.exclude) {
            handlerParams.exclude = params.resolve.exclude;
        }
    }

    return __.toNativeObject(this.native.push(handlerParams));
};

/**
 * This function resolves the differences for node between current and given branch
 *
 * @example-ref examples/node/push.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key key to resolve diff for
 * @param {string} params.target Branch to push nodes to.
 * @param {boolean} params.includeChildren also resolve dependencies for children
 *
 * @returns {object} DiffNodesResult
 */
RepoConnection.prototype.diff = function (params) {
    var handlerParams = __.newBean('com.enonic.xp.lib.node.DiffBranchesHandlerParams');
    params = params || {};
    handlerParams.nodeId = required(params, 'key');
    handlerParams.targetBranch = required(params, 'target');
    handlerParams.includeChildren = valueOrDefault(params.includeChildren, false);

    return __.toNativeObject(this.native.diff(handlerParams));
};

/**
 * This function returns a binary stream.
 *
 * @example-ref examples/node/getBinary.js
 *
 * @param {string} key Path or id to the node.
 * @param {string} binaryReference to the binary.
 *
 * @returns {*} Stream of the binary.
 */
RepoConnection.prototype.getBinary = function (params) {

    if (params.key === undefined && params.keys === undefined) {
        throw "Parameter 'key' or 'keys' is required";
    }
    var key = params.key ? params.key : null;
    var binaryReference = params.binaryReference;
    return this.native.getBinary(key, binaryReference);
};

/**
 * Rename a node or move it to a new path.
 *
 * @example-ref examples/node/move-1.js
 * @example-ref examples/node/move-2.js
 * @example-ref examples/node/move-3.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.source Path or id of the node to be moved or renamed.
 * @param {string} params.target New path or name for the node. If the target ends in slash '/', it specifies the parent path where to be moved. Otherwise it means the new desired path or name for the node.
 *
 * @returns {boolean} True if the node was successfully moved or renamed, false otherwise.
 */
RepoConnection.prototype.move = function (params) {
    var source = required(params, 'source');
    var target = required(params, 'target');
    return __.toNativeObject(this.native.move(source, target));
};

/**
 * This command queries nodes.
 *
 * @example-ref examples/node/query.js
 *
 * @param {object} params JSON with the parameters.
 * @param {number} [params.start=0] Start index (used for paging).
 * @param {number} [params.count=10] Number of contents to fetch.
 * @param {string} params.query Query expression.
 * @param {string} [params.sort] Sorting expression.
 * @param {string} [params.aggregations] Aggregations expression.
 * @returns {boolean} Result of query.
 */
RepoConnection.prototype.query = function (params) {
    var handlerParams = __.newBean('com.enonic.xp.lib.node.QueryNodeHandlerParams');
    handlerParams.start = params.start;
    handlerParams.count = params.count;
    handlerParams.query = nullOrValue(params.query);
    handlerParams.sort = nullOrValue(params.sort);
    handlerParams.aggregations = __.toScriptValue(params.aggregations);
    return __.toNativeObject(this.native.query(handlerParams));
};

/**
 * Creates a connection to a repository with a given branch and authentication info.
 *
 * @example-ref examples/node/connect.js
 *
 * @param {object} context JSON with the parameters.

 * @returns {RepoConnection} Returns a new repo-connection.
 */
exports.connect = function (context) {

    var nodeHandleContext = __.newBean('com.enonic.xp.lib.node.NodeHandleContext');
    nodeHandleContext.repoId = required(context, 'repoId');
    nodeHandleContext.branch = required(context, 'branch');

    if (context.user) {
        if (context.user.login) {
            nodeHandleContext.username = context.user.login;
        }
        if (context.user.userStore) {
            nodeHandleContext.userStore = context.user.userStore;
        }
    }

    if (context.principals) {
        nodeHandleContext.principals = context.principals;
    }

    return new RepoConnection(factory.create(nodeHandleContext));
};