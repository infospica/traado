/*!***************************************************
* mark.js v9.0.0
* https://markjs.io/
* Copyright (c) 2014–2018, Julian Kühnel
* Released under the MIT license https://git.io/vwTVl
*****************************************************/

(function (global, factory) {
	typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory(require('jquery')) :
		typeof define === 'function' && define.amd ? define(['jquery'], factory) :
			(global.Mark = factory(global.jQuery));
}(this, (function ($) {
	'use strict';

	$ = $ && $.hasOwnProperty('default') ? $['default'] : $;

	function _typeof(obj) {
		if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") {
			_typeof = function (obj) {
				return typeof obj;
			};
		} else {
			_typeof = function (obj) {
				return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj;
			};
		}

		return _typeof(obj);
	}

	function _classCallCheck(instance, Constructor) {
		if (!(instance instanceof Constructor)) {
			throw new TypeError("Cannot call a class as a function");
		}
	}

	function _defineProperties(target, props) {
		for (var i = 0; i < props.length; i++) {
			var descriptor = props[i];
			descriptor.enumerable = descriptor.enumerable || false;
			descriptor.configurable = true;
			if ("value" in descriptor) descriptor.writable = true;
			Object.defineProperty(target, descriptor.key, descriptor);
		}
	}

	function _createClass(Constructor, protoProps, staticProps) {
		if (protoProps) _defineProperties(Constructor.prototype, protoProps);
		if (staticProps) _defineProperties(Constructor, staticProps);
		return Constructor;
	}

	function _extends() {
		_extends = Object.assign || function (target) {
			for (var i = 1; i < arguments.length; i++) {
				var source = arguments[i];

				for (var key in source) {
					if (Object.prototype.hasOwnProperty.call(source, key)) {
						target[key] = source[key];
					}
				}
			}

			return target;
		};

		return _extends.apply(this, arguments);
	}

	var DOMIterator =
		/*#__PURE__*/
		function () {
			function DOMIterator(ctx) {
				var iframes = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : true;
				var exclude = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : [];
				var iframesTimeout = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : 5000;

				_classCallCheck(this, DOMIterator);

				this.ctx = ctx;
				this.iframes = iframes;
				this.exclude = exclude;
				this.iframesTimeout = iframesTimeout;
			}

			_createClass(DOMIterator, [{
				key: "getContexts",
				value: function getContexts() {
					var ctx,
						filteredCtx = [];

					if (typeof this.ctx === 'undefined' || !this.ctx) {
						ctx = [];
					} else if (NodeList.prototype.isPrototypeOf(this.ctx)) {
						ctx = Array.prototype.slice.call(this.ctx);
					} else if (Array.isArray(this.ctx)) {
						ctx = this.ctx;
					} else if (typeof this.ctx === 'string') {
						ctx = Array.prototype.slice.call(document.querySelectorAll(this.ctx));
					} else {
						ctx = [this.ctx];
					}

					ctx.forEach(function (ctx) {
						var isDescendant = filteredCtx.filter(function (contexts) {
							return contexts.contains(ctx);
						}).length > 0;

						if (filteredCtx.indexOf(ctx) === -1 && !isDescendant) {
							filteredCtx.push(ctx);
						}
					});
					return filteredCtx;
				}
			}, {
				key: "getIframeContents",
				value: function getIframeContents(ifr, successFn) {
					var errorFn = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : function () { };
					var doc;

					try {
						var ifrWin = ifr.contentWindow;
						doc = ifrWin.document;

						if (!ifrWin || !doc) {
							throw new Error('iframe inaccessible');
						}
					} catch (e) {
						errorFn();
					}

					if (doc) {
						successFn(doc);
					}
				}
			}, {
				key: "isIframeBlank",
				value: function isIframeBlank(ifr) {
					var bl = 'about:blank',
						src = ifr.getAttribute('src').trim(),
						href = ifr.contentWindow.location.href;
					return href === bl && src !== bl && src;
				}
			}, {
				key: "observeIframeLoad",
				value: function observeIframeLoad(ifr, successFn, errorFn) {
					var _this = this;

					var called = false,
						tout = null;

					var listener = function listener() {
						if (called) {
							return;
						}

						called = true;
						clearTimeout(tout);

						try {
							if (!_this.isIframeBlank(ifr)) {
								ifr.removeEventListener('load', listener);

								_this.getIframeContents(ifr, successFn, errorFn);
							}
						} catch (e) {
							errorFn();
						}
					};

					ifr.addEventListener('load', listener);
					tout = setTimeout(listener, this.iframesTimeout);
				}
			}, {
				key: "onIframeReady",
				value: function onIframeReady(ifr, successFn, errorFn) {
					try {
						if (ifr.contentWindow.document.readyState === 'complete') {
							if (this.isIframeBlank(ifr)) {
								this.observeIframeLoad(ifr, successFn, errorFn);
							} else {
								this.getIframeContents(ifr, successFn, errorFn);
							}
						} else {
							this.observeIframeLoad(ifr, successFn, errorFn);
						}
					} catch (e) {
						errorFn();
					}
				}
			}, {
				key: "waitForIframes",
				value: function waitForIframes(ctx, done) {
					var _this2 = this;

					var eachCalled = 0;
					this.forEachIframe(ctx, function () {
						return true;
					}, function (ifr) {
						eachCalled++;

						_this2.waitForIframes(ifr.querySelector('html'), function () {
							if (! --eachCalled) {
								done();
							}
						});
					}, function (handled) {
						if (!handled) {
							done();
						}
					});
				}
			}, {
				key: "forEachIframe",
				value: function forEachIframe(ctx, filter, each) {
					var _this3 = this;

					var end = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : function () { };
					var ifr = ctx.querySelectorAll('iframe'),
						open = ifr.length,
						handled = 0;
					ifr = Array.prototype.slice.call(ifr);

					var checkEnd = function checkEnd() {
						if (--open <= 0) {
							end(handled);
						}
					};

					if (!open) {
						checkEnd();
					}

					ifr.forEach(function (ifr) {
						if (DOMIterator.matches(ifr, _this3.exclude)) {
							checkEnd();
						} else {
							_this3.onIframeReady(ifr, function (con) {
								if (filter(ifr)) {
									handled++;
									each(con);
								}

								checkEnd();
							}, checkEnd);
						}
					});
				}
			}, {
				key: "createIterator",
				value: function createIterator(ctx, whatToShow, filter) {
					return document.createNodeIterator(ctx, whatToShow, filter, false);
				}
			}, {
				key: "createInstanceOnIframe",
				value: function createInstanceOnIframe(contents) {
					return new DOMIterator(contents.querySelector('html'), this.iframes);
				}
			}, {
				key: "compareNodeIframe",
				value: function compareNodeIframe(node, prevNode, ifr) {
					var compCurr = node.compareDocumentPosition(ifr),
						prev = Node.DOCUMENT_POSITION_PRECEDING;

					if (compCurr & prev) {
						if (prevNode !== null) {
							var compPrev = prevNode.compareDocumentPosition(ifr),
								after = Node.DOCUMENT_POSITION_FOLLOWING;

							if (compPrev & after) {
								return true;
							}
						} else {
							return true;
						}
					}

					return false;
				}
			}, {
				key: "getIteratorNode",
				value: function getIteratorNode(itr) {
					var prevNode = itr.previousNode();
					var node;

					if (prevNode === null) {
						node = itr.nextNode();
					} else {
						node = itr.nextNode() && itr.nextNode();
					}

					return {
						prevNode: prevNode,
						node: node
					};
				}
			}, {
				key: "checkIframeFilter",
				value: function checkIframeFilter(node, prevNode, currIfr, ifr) {
					var key = false,
						handled = false;
					ifr.forEach(function (ifrDict, i) {
						if (ifrDict.val === currIfr) {
							key = i;
							handled = ifrDict.handled;
						}
					});

					if (this.compareNodeIframe(node, prevNode, currIfr)) {
						if (key === false && !handled) {
							ifr.push({
								val: currIfr,
								handled: true
							});
						} else if (key !== false && !handled) {
							ifr[key].handled = true;
						}

						return true;
					}

					if (key === false) {
						ifr.push({
							val: currIfr,
							handled: false
						});
					}

					return false;
				}
			}, {
				key: "handleOpenIframes",
				value: function handleOpenIframes(ifr, whatToShow, eCb, fCb) {
					var _this4 = this;

					ifr.forEach(function (ifrDict) {
						if (!ifrDict.handled) {
							_this4.getIframeContents(ifrDict.val, function (con) {
								_this4.createInstanceOnIframe(con).forEachNode(whatToShow, eCb, fCb);
							});
						}
					});
				}
			}, {
				key: "iterateThroughNodes",
				value: function iterateThroughNodes(whatToShow, ctx, eachCb, filterCb, doneCb) {
					var _this5 = this;

					var itr = this.createIterator(ctx, whatToShow, filterCb);

					var ifr = [],
						elements = [],
						node,
						prevNode,
						retrieveNodes = function retrieveNodes() {
							var _this5$getIteratorNod = _this5.getIteratorNode(itr);

							prevNode = _this5$getIteratorNod.prevNode;
							node = _this5$getIteratorNod.node;
							return node;
						};

					while (retrieveNodes()) {
						if (this.iframes) {
							this.forEachIframe(ctx, function (currIfr) {
								return _this5.checkIframeFilter(node, prevNode, currIfr, ifr);
							}, function (con) {
								_this5.createInstanceOnIframe(con).forEachNode(whatToShow, function (ifrNode) {
									return elements.push(ifrNode);
								}, filterCb);
							});
						}

						elements.push(node);
					}

					elements.forEach(function (node) {
						eachCb(node);
					});

					if (this.iframes) {
						this.handleOpenIframes(ifr, whatToShow, eachCb, filterCb);
					}

					doneCb();
				}
			}, {
				key: "forEachNode",
				value: function forEachNode(whatToShow, each, filter) {
					var _this6 = this;

					var done = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : function () { };
					var contexts = this.getContexts();
					var open = contexts.length;

					if (!open) {
						done();
					}

					contexts.forEach(function (ctx) {
						var ready = function ready() {
							_this6.iterateThroughNodes(whatToShow, ctx, each, filter, function () {
								if (--open <= 0) {
									done();
								}
							});
						};

						if (_this6.iframes) {
							_this6.waitForIframes(ctx, ready);
						} else {
							ready();
						}
					});
				}
			}], [{
				key: "matches",
				value: function matches(element, selector) {
					var selectors = typeof selector === 'string' ? [selector] : selector,
						fn = element.matches || element.matchesSelector || element.msMatchesSelector || element.mozMatchesSelector || element.oMatchesSelector || element.webkitMatchesSelector;

					if (fn) {
						var match = false;
						selectors.every(function (sel) {
							if (fn.call(element, sel)) {
								match = true;
								return false;
							}

							return true;
						});
						return match;
					} else {
						return false;
					}
				}
			}]);

			return DOMIterator;
		}();

	var RegExpCreator =
		/*#__PURE__*/
		function () {
			function RegExpCreator(options) {
				_classCallCheck(this, RegExpCreator);

				this.opt = _extends({}, {
					'diacritics': true,
					'synonyms': {},
					'accuracy': 'partially',
					'caseSensitive': false,
					'ignoreJoiners': false,
					'ignorePunctuation': [],
					'wildcards': 'disabled'
				}, options);
			}

			_createClass(RegExpCreator, [{
				key: "create",
				value: function create(str) {
					if (this.opt.wildcards !== 'disabled') {
						str = this.setupWildcardsRegExp(str);
					}

					str = this.escapeStr(str);

					if (Object.keys(this.opt.synonyms).length) {
						str = this.createSynonymsRegExp(str);
					}

					if (this.opt.ignoreJoiners || this.opt.ignorePunctuation.length) {
						str = this.setupIgnoreJoinersRegExp(str);
					}

					if (this.opt.diacritics) {
						str = this.createDiacriticsRegExp(str);
					}

					str = this.createMergedBlanksRegExp(str);

					if (this.opt.ignoreJoiners || this.opt.ignorePunctuation.length) {
						str = this.createJoinersRegExp(str);
					}

					if (this.opt.wildcards !== 'disabled') {
						str = this.createWildcardsRegExp(str);
					}

					str = this.createAccuracyRegExp(str);
					return new RegExp(str, "gm".concat(this.opt.caseSensitive ? '' : 'i'));
				}
			}, {
				key: "sortByLength",
				value: function sortByLength(arry) {
					return arry.sort(function (a, b) {
						return a.length === b.length ? a > b ? 1 : -1 : b.length - a.length;
					});
				}
			}, {
				key: "escapeStr",
				value: function escapeStr(str) {
					return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, '\\$&');
				}
			}, {
				key: "createSynonymsRegExp",
				value: function createSynonymsRegExp(str) {
					var _this = this;

					var syn = this.opt.synonyms,
						sens = this.opt.caseSensitive ? '' : 'i',
						joinerPlaceholder = this.opt.ignoreJoiners || this.opt.ignorePunctuation.length ? "\0" : '';

					for (var index in syn) {
						if (syn.hasOwnProperty(index)) {
							var keys = Array.isArray(syn[index]) ? syn[index] : [syn[index]];
							keys.unshift(index);
							keys = this.sortByLength(keys).map(function (key) {
								if (_this.opt.wildcards !== 'disabled') {
									key = _this.setupWildcardsRegExp(key);
								}

								key = _this.escapeStr(key);
								return key;
							}).filter(function (k) {
								return k !== '';
							});

							if (keys.length > 1) {
								str = str.replace(new RegExp("(".concat(keys.map(function (k) {
									return _this.escapeStr(k);
								}).join('|'), ")"), "gm".concat(sens)), joinerPlaceholder + "(".concat(keys.map(function (k) {
									return _this.processSynonyms(k);
								}).join('|'), ")") + joinerPlaceholder);
							}
						}
					}

					return str;
				}
			}, {
				key: "processSynonyms",
				value: function processSynonyms(str) {
					if (this.opt.ignoreJoiners || this.opt.ignorePunctuation.length) {
						str = this.setupIgnoreJoinersRegExp(str);
					}

					return str;
				}
			}, {
				key: "setupWildcardsRegExp",
				value: function setupWildcardsRegExp(str) {
					str = str.replace(/(?:\\)*\?/g, function (val) {
						return val.charAt(0) === '\\' ? '?' : "\x01";
					});
					return str.replace(/(?:\\)*\*/g, function (val) {
						return val.charAt(0) === '\\' ? '*' : "\x02";
					});
				}
			}, {
				key: "createWildcardsRegExp",
				value: function createWildcardsRegExp(str) {
					var spaces = this.opt.wildcards === 'withSpaces';
					return str.replace(/\u0001/g, spaces ? '[\\S\\s]?' : '\\S?').replace(/\u0002/g, spaces ? '[\\S\\s]*?' : '\\S*');
				}
			}, {
				key: "setupIgnoreJoinersRegExp",
				value: function setupIgnoreJoinersRegExp(str) {
					return str.replace(/[^(|)\\]/g, function (val, indx, original) {
						var nextChar = original.charAt(indx + 1);

						if (/[(|)\\]/.test(nextChar) || nextChar === '') {
							return val;
						} else {
							return val + "\0";
						}
					});
				}
			}, {
				key: "createJoinersRegExp",
				value: function createJoinersRegExp(str) {
					var joiner = [];
					var ignorePunctuation = this.opt.ignorePunctuation;

					if (Array.isArray(ignorePunctuation) && ignorePunctuation.length) {
						joiner.push(this.escapeStr(ignorePunctuation.join('')));
					}

					if (this.opt.ignoreJoiners) {
						joiner.push("\\u00ad\\u200b\\u200c\\u200d");
					}

					return joiner.length ? str.split(/\u0000+/).join("[".concat(joiner.join(''), "]*")) : str;
				}
			}, {
				key: "createDiacriticsRegExp",
				value: function createDiacriticsRegExp(str) {
					var sens = this.opt.caseSensitive ? '' : 'i',
						dct = this.opt.caseSensitive ? ['aàáảãạăằắẳẵặâầấẩẫậäåāą', 'AÀÁẢÃẠĂẰẮẲẴẶÂẦẤẨẪẬÄÅĀĄ', 'cçćč', 'CÇĆČ', 'dđď', 'DĐĎ', 'eèéẻẽẹêềếểễệëěēę', 'EÈÉẺẼẸÊỀẾỂỄỆËĚĒĘ', 'iìíỉĩịîïī', 'IÌÍỈĨỊÎÏĪ', 'lł', 'LŁ', 'nñňń', 'NÑŇŃ', 'oòóỏõọôồốổỗộơởỡớờợöøō', 'OÒÓỎÕỌÔỒỐỔỖỘƠỞỠỚỜỢÖØŌ', 'rř', 'RŘ', 'sšśșş', 'SŠŚȘŞ', 'tťțţ', 'TŤȚŢ', 'uùúủũụưừứửữựûüůū', 'UÙÚỦŨỤƯỪỨỬỮỰÛÜŮŪ', 'yýỳỷỹỵÿ', 'YÝỲỶỸỴŸ', 'zžżź', 'ZŽŻŹ'] : ['aàáảãạăằắẳẵặâầấẩẫậäåāąAÀÁẢÃẠĂẰẮẲẴẶÂẦẤẨẪẬÄÅĀĄ', 'cçćčCÇĆČ', 'dđďDĐĎ', 'eèéẻẽẹêềếểễệëěēęEÈÉẺẼẸÊỀẾỂỄỆËĚĒĘ', 'iìíỉĩịîïīIÌÍỈĨỊÎÏĪ', 'lłLŁ', 'nñňńNÑŇŃ', 'oòóỏõọôồốổỗộơởỡớờợöøōOÒÓỎÕỌÔỒỐỔỖỘƠỞỠỚỜỢÖØŌ', 'rřRŘ', 'sšśșşSŠŚȘŞ', 'tťțţTŤȚŢ', 'uùúủũụưừứửữựûüůūUÙÚỦŨỤƯỪỨỬỮỰÛÜŮŪ', 'yýỳỷỹỵÿYÝỲỶỸỴŸ', 'zžżźZŽŻŹ'];
					var handled = [];
					str.split('').forEach(function (ch) {
						dct.every(function (dct) {
							if (dct.indexOf(ch) !== -1) {
								if (handled.indexOf(dct) > -1) {
									return false;
								}

								str = str.replace(new RegExp("[".concat(dct, "]"), "gm".concat(sens)), "[".concat(dct, "]"));
								handled.push(dct);
							}

							return true;
						});
					});
					return str;
				}
			}, {
				key: "createMergedBlanksRegExp",
				value: function createMergedBlanksRegExp(str) {
					return str.replace(/[\s]+/gmi, '[\\s]+');
				}
			}, {
				key: "createAccuracyRegExp",
				value: function createAccuracyRegExp(str) {
					var _this2 = this;

					var chars = '!"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~¡¿';
					var acc = this.opt.accuracy,
						val = typeof acc === 'string' ? acc : acc.value,
						ls = typeof acc === 'string' ? [] : acc.limiters,
						lsJoin = '';
					ls.forEach(function (limiter) {
						lsJoin += "|".concat(_this2.escapeStr(limiter));
					});

					switch (val) {
						case 'partially':
						default:
							return "()(".concat(str, ")");

						case 'complementary':
							lsJoin = '\\s' + (lsJoin ? lsJoin : this.escapeStr(chars));
							return "()([^".concat(lsJoin, "]*").concat(str, "[^").concat(lsJoin, "]*)");

						case 'exactly':
							return "(^|\\s".concat(lsJoin, ")(").concat(str, ")(?=$|\\s").concat(lsJoin, ")");
					}
				}
			}]);

			return RegExpCreator;
		}();

	var Mark =
		/*#__PURE__*/
		function () {
			function Mark(ctx) {
				_classCallCheck(this, Mark);

				this.ctx = ctx;
				this.ie = false;
				var ua = window.navigator.userAgent;

				if (ua.indexOf('MSIE') > -1 || ua.indexOf('Trident') > -1) {
					this.ie = true;
				}
			}

			_createClass(Mark, [{
				key: "log",
				value: function log(msg) {
					var level = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 'debug';
					var log = this.opt.log;

					if (!this.opt.debug) {
						return;
					}

					if (_typeof(log) === 'object' && typeof log[level] === 'function') {
						log[level]("mark.js: ".concat(msg));
					}
				}
			}, {
				key: "getSeparatedKeywords",
				value: function getSeparatedKeywords(sv) {
					var _this = this;

					var stack = [];
					sv.forEach(function (kw) {
						if (!_this.opt.separateWordSearch) {
							if (kw.trim() && stack.indexOf(kw) === -1) {
								stack.push(kw);
							}
						} else {
							kw.split(' ').forEach(function (kwSplitted) {
								if (kwSplitted.trim() && stack.indexOf(kwSplitted) === -1) {
									stack.push(kwSplitted);
								}
							});
						}
					});
					return {
						'keywords': stack.sort(function (a, b) {
							return b.length - a.length;
						}),
						'length': stack.length
					};
				}
			}, {
				key: "isNumeric",
				value: function isNumeric(value) {
					return Number(parseFloat(value)) == value;
				}
			}, {
				key: "checkRanges",
				value: function checkRanges(array) {
					var _this2 = this;

					if (!Array.isArray(array) || Object.prototype.toString.call(array[0]) !== '[object Object]') {
						this.log('markRanges() will only accept an array of objects');
						this.opt.noMatch(array);
						return [];
					}

					var stack = [];
					var last = 0;
					array.sort(function (a, b) {
						return a.start - b.start;
					}).forEach(function (item) {
						var _this2$callNoMatchOnI = _this2.callNoMatchOnInvalidRanges(item, last),
							start = _this2$callNoMatchOnI.start,
							end = _this2$callNoMatchOnI.end,
							valid = _this2$callNoMatchOnI.valid;

						if (valid) {
							item.start = start;
							item.length = end - start;
							stack.push(item);
							last = end;
						}
					});
					return stack;
				}
			}, {
				key: "callNoMatchOnInvalidRanges",
				value: function callNoMatchOnInvalidRanges(range, last) {
					var start,
						end,
						valid = false;

					if (range && typeof range.start !== 'undefined') {
						start = parseInt(range.start, 10);
						end = start + parseInt(range.length, 10);

						if (this.isNumeric(range.start) && this.isNumeric(range.length) && end - last > 0 && end - start > 0) {
							valid = true;
						} else {
							this.log('Ignoring invalid or overlapping range: ' + "".concat(JSON.stringify(range)));
							this.opt.noMatch(range);
						}
					} else {
						this.log("Ignoring invalid range: ".concat(JSON.stringify(range)));
						this.opt.noMatch(range);
					}

					return {
						start: start,
						end: end,
						valid: valid
					};
				}
			}, {
				key: "checkWhitespaceRanges",
				value: function checkWhitespaceRanges(range, originalLength, string) {
					var end,
						valid = true,
						max = string.length,
						offset = originalLength - max,
						start = parseInt(range.start, 10) - offset;
					start = start > max ? max : start;
					end = start + parseInt(range.length, 10);

					if (end > max) {
						end = max;
						this.log("End range automatically set to the max value of ".concat(max));
					}

					if (start < 0 || end - start < 0 || start > max || end > max) {
						valid = false;
						this.log("Invalid range: ".concat(JSON.stringify(range)));
						this.opt.noMatch(range);
					} else if (string.substring(start, end).replace(/\s+/g, '') === '') {
						valid = false;
						this.log('Skipping whitespace only range: ' + JSON.stringify(range));
						this.opt.noMatch(range);
					}

					return {
						start: start,
						end: end,
						valid: valid
					};
				}
			}, {
				key: "getTextNodes",
				value: function getTextNodes(cb) {
					var _this3 = this;

					var val = '',
						nodes = [];
					this.iterator.forEachNode(NodeFilter.SHOW_TEXT, function (node) {
						nodes.push({
							start: val.length,
							end: (val += node.textContent).length,
							node: node
						});
					}, function (node) {
						if (_this3.matchesExclude(node.parentNode)) {
							return NodeFilter.FILTER_REJECT;
						} else {
							return NodeFilter.FILTER_ACCEPT;
						}
					}, function () {
						cb({
							value: val,
							nodes: nodes
						});
					});
				}
			}, {
				key: "matchesExclude",
				value: function matchesExclude(el) {
					return DOMIterator.matches(el, this.opt.exclude.concat(['script', 'style', 'title', 'head', 'html']));
				}
			}, {
				key: "wrapRangeInTextNode",
				value: function wrapRangeInTextNode(node, start, end) {
					var hEl = !this.opt.element ? 'mark' : this.opt.element,
						startNode = node.splitText(start),
						ret = startNode.splitText(end - start);
					var repl = document.createElement(hEl);
					repl.setAttribute('data-markjs', 'true');

					if (this.opt.className) {
						repl.setAttribute('class', this.opt.className);
					}

					repl.textContent = startNode.textContent;
					startNode.parentNode.replaceChild(repl, startNode);
					return ret;
				}
			}, {
				key: "wrapRangeInMappedTextNode",
				value: function wrapRangeInMappedTextNode(dict, start, end, filterCb, eachCb) {
					var _this4 = this;

					dict.nodes.every(function (n, i) {
						var sibl = dict.nodes[i + 1];

						if (typeof sibl === 'undefined' || sibl.start > start) {
							if (!filterCb(n.node)) {
								return false;
							}

							var s = start - n.start,
								e = (end > n.end ? n.end : end) - n.start,
								startStr = dict.value.substr(0, n.start),
								endStr = dict.value.substr(e + n.start);
							n.node = _this4.wrapRangeInTextNode(n.node, s, e);
							dict.value = startStr + endStr;
							dict.nodes.forEach(function (k, j) {
								if (j >= i) {
									if (dict.nodes[j].start > 0 && j !== i) {
										dict.nodes[j].start -= e;
									}

									dict.nodes[j].end -= e;
								}
							});
							end -= e;
							eachCb(n.node.previousSibling, n.start);

							if (end > n.end) {
								start = n.end;
							} else {
								return false;
							}
						}

						return true;
					});
				}
			}, {
				key: "wrapGroups",
				value: function wrapGroups(node, pos, len, eachCb) {
					node = this.wrapRangeInTextNode(node, pos, pos + len);
					eachCb(node.previousSibling);
					return node;
				}
			}, {
				key: "separateGroups",
				value: function separateGroups(node, match, matchIdx, filterCb, eachCb) {
					var matchLen = match.length;

					for (var i = 1; i < matchLen; i++) {
						var pos = node.textContent.indexOf(match[i]);

						if (match[i] && pos > -1 && filterCb(match[i], node)) {
							node = this.wrapGroups(node, pos, match[i].length, eachCb);
						}
					}

					return node;
				}
			}, {
				key: "wrapMatches",
				value: function wrapMatches(regex, ignoreGroups, filterCb, eachCb, endCb) {
					var _this5 = this;

					var matchIdx = ignoreGroups === 0 ? 0 : ignoreGroups + 1;
					this.getTextNodes(function (dict) {
						dict.nodes.forEach(function (node) {
							node = node.node;
							var match;

							while ((match = regex.exec(node.textContent)) !== null && match[matchIdx] !== '') {
								if (_this5.opt.separateGroups) {
									node = _this5.separateGroups(node, match, matchIdx, filterCb, eachCb);
								} else {
									if (!filterCb(match[matchIdx], node)) {
										continue;
									}

									var pos = match.index;

									if (matchIdx !== 0) {
										for (var i = 1; i < matchIdx; i++) {
											pos += match[i].length;
										}
									}

									node = _this5.wrapGroups(node, pos, match[matchIdx].length, eachCb);
								}

								regex.lastIndex = 0;
							}
						});
						endCb();
					});
				}
			}, {
				key: "wrapMatchesAcrossElements",
				value: function wrapMatchesAcrossElements(regex, ignoreGroups, filterCb, eachCb, endCb) {
					var _this6 = this;

					var matchIdx = ignoreGroups === 0 ? 0 : ignoreGroups + 1;
					this.getTextNodes(function (dict) {
						var match;

						while ((match = regex.exec(dict.value)) !== null && match[matchIdx] !== '') {
							var start = match.index;

							if (matchIdx !== 0) {
								for (var i = 1; i < matchIdx; i++) {
									start += match[i].length;
								}
							}

							var end = start + match[matchIdx].length;

							_this6.wrapRangeInMappedTextNode(dict, start, end, function (node) {
								return filterCb(match[matchIdx], node);
							}, function (node, lastIndex) {
								regex.lastIndex = lastIndex;
								eachCb(node);
							});
						}

						endCb();
					});
				}
			}, {
				key: "wrapRangeFromIndex",
				value: function wrapRangeFromIndex(ranges, filterCb, eachCb, endCb) {
					var _this7 = this;

					this.getTextNodes(function (dict) {
						var originalLength = dict.value.length;
						ranges.forEach(function (range, counter) {
							var _this7$checkWhitespac = _this7.checkWhitespaceRanges(range, originalLength, dict.value),
								start = _this7$checkWhitespac.start,
								end = _this7$checkWhitespac.end,
								valid = _this7$checkWhitespac.valid;

							if (valid) {
								_this7.wrapRangeInMappedTextNode(dict, start, end, function (node) {
									return filterCb(node, range, dict.value.substring(start, end), counter);
								}, function (node) {
									eachCb(node, range);
								});
							}
						});
						endCb();
					});
				}
			}, {
				key: "unwrapMatches",
				value: function unwrapMatches(node) {
					var parent = node.parentNode;
					var docFrag = document.createDocumentFragment();

					while (node.firstChild) {
						docFrag.appendChild(node.removeChild(node.firstChild));
					}

					parent.replaceChild(docFrag, node);

					if (!this.ie) {
						parent.normalize();
					} else {
						this.normalizeTextNode(parent);
					}
				}
			}, {
				key: "normalizeTextNode",
				value: function normalizeTextNode(node) {
					if (!node) {
						return;
					}

					if (node.nodeType === 3) {
						while (node.nextSibling && node.nextSibling.nodeType === 3) {
							node.nodeValue += node.nextSibling.nodeValue;
							node.parentNode.removeChild(node.nextSibling);
						}
					} else {
						this.normalizeTextNode(node.firstChild);
					}

					this.normalizeTextNode(node.nextSibling);
				}
			}, {
				key: "markRegExp",
				value: function markRegExp(regexp, opt) {
					var _this8 = this;

					this.opt = opt;
					this.log("Searching with expression \"".concat(regexp, "\""));
					var totalMatches = 0,
						fn = 'wrapMatches';

					var eachCb = function eachCb(element) {
						totalMatches++;

						_this8.opt.each(element);
					};

					if (this.opt.acrossElements) {
						fn = 'wrapMatchesAcrossElements';
					}

					this[fn](regexp, this.opt.ignoreGroups, function (match, node) {
						return _this8.opt.filter(node, match, totalMatches);
					}, eachCb, function () {
						if (totalMatches === 0) {
							_this8.opt.noMatch(regexp);
						}

						_this8.opt.done(totalMatches);
					});
				}
			}, {
				key: "mark",
				value: function mark(sv, opt) {
					var _this9 = this;

					this.opt = opt;
					var totalMatches = 0,
						fn = 'wrapMatches';

					var _this$getSeparatedKey = this.getSeparatedKeywords(typeof sv === 'string' ? [sv] : sv),
						kwArr = _this$getSeparatedKey.keywords,
						kwArrLen = _this$getSeparatedKey.length,
						handler = function handler(kw) {
							var regex = new RegExpCreator(_this9.opt).create(kw);
							var matches = 0;

							_this9.log("Searching with expression \"".concat(regex, "\""));

							_this9[fn](regex, 1, function (term, node) {
								return _this9.opt.filter(node, kw, totalMatches, matches);
							}, function (element) {
								matches++;
								totalMatches++;

								_this9.opt.each(element);
							}, function () {
								if (matches === 0) {
									_this9.opt.noMatch(kw);
								}

								if (kwArr[kwArrLen - 1] === kw) {
									_this9.opt.done(totalMatches);
								} else {
									handler(kwArr[kwArr.indexOf(kw) + 1]);
								}
							});
						};

					if (this.opt.acrossElements) {
						fn = 'wrapMatchesAcrossElements';
					}

					if (kwArrLen === 0) {
						this.opt.done(totalMatches);
					} else {
						handler(kwArr[0]);
					}
				}
			}, {
				key: "markRanges",
				value: function markRanges(rawRanges, opt) {
					var _this10 = this;

					this.opt = opt;
					var totalMatches = 0,
						ranges = this.checkRanges(rawRanges);

					if (ranges && ranges.length) {
						this.log('Starting to mark with the following ranges: ' + JSON.stringify(ranges));
						this.wrapRangeFromIndex(ranges, function (node, range, match, counter) {
							return _this10.opt.filter(node, range, match, counter);
						}, function (element, range) {
							totalMatches++;

							_this10.opt.each(element, range);
						}, function () {
							_this10.opt.done(totalMatches);
						});
					} else {
						this.opt.done(totalMatches);
					}
				}
			}, {
				key: "unmark",
				value: function unmark(opt) {
					var _this11 = this;

					this.opt = opt;
					var sel = this.opt.element ? this.opt.element : '*';
					sel += '[data-markjs]';

					if (this.opt.className) {
						sel += ".".concat(this.opt.className);
					}

					this.log("Removal selector \"".concat(sel, "\""));
					this.iterator.forEachNode(NodeFilter.SHOW_ELEMENT, function (node) {
						_this11.unwrapMatches(node);
					}, function (node) {
						var matchesSel = DOMIterator.matches(node, sel),
							matchesExclude = _this11.matchesExclude(node);

						if (!matchesSel || matchesExclude) {
							return NodeFilter.FILTER_REJECT;
						} else {
							return NodeFilter.FILTER_ACCEPT;
						}
					}, this.opt.done);
				}
			}, {
				key: "opt",
				set: function set(val) {
					this._opt = _extends({}, {
						'element': '',
						'className': '',
						'exclude': [],
						'iframes': false,
						'iframesTimeout': 5000,
						'separateWordSearch': true,
						'acrossElements': false,
						'ignoreGroups': 0,
						'each': function each() { },
						'noMatch': function noMatch() { },
						'filter': function filter() {
							return true;
						},
						'done': function done() { },
						'debug': false,
						'log': window.console
					}, val);
				},
				get: function get() {
					return this._opt;
				}
			}, {
				key: "iterator",
				get: function get() {
					return new DOMIterator(this.ctx, this.opt.iframes, this.opt.exclude, this.opt.iframesTimeout);
				}
			}]);

			return Mark;
		}();

	$.fn.mark = function (sv, opt) {
		new Mark(this.get()).mark(sv, opt);
		return this;
	};

	$.fn.markRegExp = function (regexp, opt) {
		new Mark(this.get()).markRegExp(regexp, opt);
		return this;
	};

	$.fn.markRanges = function (ranges, opt) {
		new Mark(this.get()).markRanges(ranges, opt);
		return this;
	};

	$.fn.unmark = function (opt) {
		new Mark(this.get()).unmark(opt);
		return this;
	};

	return $;

})));

/*global $, jQuery, alert, console*/

var TRUTH = [true, 'true', 1],
	NULL = [null, 'null', undefined, 'undefined', ''],
	kb = {
		TAB: 9,
		ESC: 27,
		ENTER: 13,
		UP: 38,
		DOWN: 40,
		LEFT: 37,
		RIGHT: 39
	},
	focusables = 'a,input,button,select,textarea';

String.prototype.toCapitalCase = String.prototype.toCapitalCase || function () {
	return this.charAt(0).toUpperCase() + this.slice(1);
}

function getBoolean(a) { return TRUTH.some(function (t) { return t === a; }); }
function isNull(v) { return (v === null || v === 'null' || v === '' || v === undefined || v === 'undefined'); }
function onlyUnique(value, index, self) { return self.indexOf(value) === index; }
function abs(n) { "use strict"; return Math.abs(n); }
function valRound(n, r) {
	"use strict";
	r = Math.pow(10, parseInt(r));
	r = (isNaN(r)) ? 100 : r;
	n = parseFloat(n);
	n = (isNaN(n)) ? 0 : n;
	n = $.isNumeric(n) ? Math.round(n * r) / r : n;
	return n;
}
function getRandomInt(min, max) {
	min = Math.ceil(min);
	max = Math.floor(max);
	return Math.floor(Math.random() * (max - min + 1)) + min; // The maximum is inclusive and the minimum is inclusive 
}
function getRandomStr(l) {
	l = l || 8;
	return Math.random().toString(36).substr(2, l);
}

// Animations
var Ai;
(function (Ai) {
	Ai[Ai["bounceIn"] = 0] = "bounceIn";
	Ai[Ai["bounceInDown"] = 1] = "bounceInDown";
	Ai[Ai["bounceInLeft"] = 2] = "bounceInLeft";
	Ai[Ai["bounceInRight"] = 3] = "bounceInRight";
	Ai[Ai["bounceInUp"] = 4] = "bounceInUp";
	Ai[Ai["fadeIn"] = 5] = "fadeIn";
	Ai[Ai["fadeInDown"] = 6] = "fadeInDown";
	Ai[Ai["fadeInDownBig"] = 7] = "fadeInDownBig";
	Ai[Ai["fadeInLeft"] = 8] = "fadeInLeft";
	Ai[Ai["fadeInLeftBig"] = 9] = "fadeInLeftBig";
	Ai[Ai["fadeInRight"] = 10] = "fadeInRight";
	Ai[Ai["fadeInRightBig"] = 11] = "fadeInRightBig";
	Ai[Ai["fadeInUp"] = 12] = "fadeInUp";
	Ai[Ai["fadeInUpBig"] = 13] = "fadeInUpBig";
	Ai[Ai["flip"] = 14] = "flip";
	Ai[Ai["flipInX"] = 15] = "flipInX";
	Ai[Ai["flipInY"] = 16] = "flipInY";
	Ai[Ai["lightSpeedIn"] = 17] = "lightSpeedIn";
	Ai[Ai["rotateIn"] = 18] = "rotateIn";
	Ai[Ai["rotateInDownLeft"] = 19] = "rotateInDownLeft";
	Ai[Ai["rotateInDownRight"] = 20] = "rotateInDownRight";
	Ai[Ai["rotateInUpLeft"] = 21] = "rotateInUpLeft";
	Ai[Ai["rotateInUpRight"] = 22] = "rotateInUpRight";
	Ai[Ai["slideInUp"] = 23] = "slideInUp";
	Ai[Ai["slideInDown"] = 24] = "slideInDown";
	Ai[Ai["slideInLeft"] = 25] = "slideInLeft";
	Ai[Ai["slideInRight"] = 26] = "slideInRight";
	Ai[Ai["zoomIn"] = 27] = "zoomIn";
	Ai[Ai["zoomInDown"] = 28] = "zoomInDown";
	Ai[Ai["zoomInLeft"] = 29] = "zoomInLeft";
	Ai[Ai["zoomInRight"] = 30] = "zoomInRight";
	Ai[Ai["zoomInUp"] = 31] = "zoomInUp";
	Ai[Ai["hinge"] = 32] = "hinge";
	Ai[Ai["jackInTheBox"] = 33] = "jackInTheBox";
	Ai[Ai["rollIn"] = 34] = "rollIn";
})(Ai || (Ai = {}));
var Ao;
(function (Ao) {
	Ao[Ao["bounceOut"] = 0] = "bounceOut";
	Ao[Ao["bounceOutDown"] = 1] = "bounceOutDown";
	Ao[Ao["bounceOutLeft"] = 2] = "bounceOutLeft";
	Ao[Ao["bounceOutRight"] = 3] = "bounceOutRight";
	Ao[Ao["bounceOutUp"] = 4] = "bounceOutUp";
	Ao[Ao["fadeOut"] = 5] = "fadeOut";
	Ao[Ao["fadeOutDown"] = 6] = "fadeOutDown";
	Ao[Ao["fadeOutDownBig"] = 7] = "fadeOutDownBig";
	Ao[Ao["fadeOutLeft"] = 8] = "fadeOutLeft";
	Ao[Ao["fadeOutLeftBig"] = 9] = "fadeOutLeftBig";
	Ao[Ao["fadeOutRight"] = 10] = "fadeOutRight";
	Ao[Ao["fadeOutRightBig"] = 11] = "fadeOutRightBig";
	Ao[Ao["fadeOutUp"] = 12] = "fadeOutUp";
	Ao[Ao["fadeOutUpBig"] = 13] = "fadeOutUpBig";
	Ao[Ao["flipOutX"] = 14] = "flipOutX";
	Ao[Ao["flipOutY"] = 15] = "flipOutY";
	Ao[Ao["lightSpeedOut"] = 16] = "lightSpeedOut";
	Ao[Ao["rotateOut"] = 17] = "rotateOut";
	Ao[Ao["rotateOutDownLeft"] = 18] = "rotateOutDownLeft";
	Ao[Ao["rotateOutDownRight"] = 19] = "rotateOutDownRight";
	Ao[Ao["rotateOutUpLeft"] = 20] = "rotateOutUpLeft";
	Ao[Ao["rotateOutUpRight"] = 21] = "rotateOutUpRight";
	Ao[Ao["slideOutUp"] = 22] = "slideOutUp";
	Ao[Ao["slideOutDown"] = 23] = "slideOutDown";
	Ao[Ao["slideOutLeft"] = 24] = "slideOutLeft";
	Ao[Ao["slideOutRight"] = 25] = "slideOutRight";
	Ao[Ao["zoomOut"] = 26] = "zoomOut";
	Ao[Ao["zoomOutDown"] = 27] = "zoomOutDown";
	Ao[Ao["zoomOutLeft"] = 28] = "zoomOutLeft";
	Ao[Ao["zoomOutRight"] = 29] = "zoomOutRight";
	Ao[Ao["zoomOutUp"] = 30] = "zoomOutUp";
	Ao[Ao["rollOut"] = 31] = "rollOut";
})(Ao || (Ao = {}));
var Aa;
(function (Aa) {
	Aa[Aa["bounce"] = 0] = "bounce";
	Aa[Aa["flash"] = 1] = "flash";
	Aa[Aa["pulse"] = 2] = "pulse";
	Aa[Aa["rubberBand"] = 3] = "rubberBand";
	Aa[Aa["shake"] = 4] = "shake";
	Aa[Aa["swing"] = 5] = "swing";
	Aa[Aa["tada"] = 6] = "tada";
	Aa[Aa["wobble"] = 7] = "wobble";
	Aa[Aa["jello"] = 8] = "jello";
})(Aa || (Aa = {}));
// /* Enum Size */
// Object.keys(animation.in).length / 2
// Object.keys(animation.out).length / 2
// Object.keys(animation.adv).length / 2
var animation = {
	adv: Aa,
	in: Ai,
	out: Ao
};

/**
* BROWSER FINDER
*/
var matched, browser;

jQuery.uaMatch = function (ua) {
	ua = ua.toLowerCase();

	var match = /(chrome)[ \/]([\w.]+)/.exec(ua) ||
		/(webkit)[ \/]([\w.]+)/.exec(ua) ||
		/(opera)(?:.*version|)[ \/]([\w.]+)/.exec(ua) ||
		/(msie)[\s?]([\w.]+)/.exec(ua) ||
		/(trident)(?:.*? rv:([\w.]+)|)/.exec(ua) ||
		ua.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(ua) ||
		[];

	return {
		browser: match[1] || "",
		version: match[2] || "0"
	};
};
matched = jQuery.uaMatch(navigator.userAgent);
//IE 11+ fix (Trident) 
matched.browser = matched.browser == 'trident' ? 'msie' : matched.browser;
browser = {};
if (matched.browser) {
	browser[matched.browser] = true;
	browser.version = matched.version;
}
// Chrome is Webkit, but Webkit is also Safari.
if (browser.chrome) {
	browser.webkit = true;
} else if (browser.webkit) {
	browser.safari = true;
}
jQuery.browser = browser;

function elementSupportsAttribute(element, attribute) {
	var test = document.createElement(element);
	if (attribute in test) {
		return true;
	} else {
		return false;
	}
}

var searchFocusable = function (ob, focusable) {
	focusable = (focusable === undefined) ? focusables : focusable;
	if (
		$($(focusable)[$(focusable).index(ob) + 1]).is(':hidden') ||
		$($(focusable)[$(focusable).index(ob) + 1]).is(':disabled') ||
		$($(focusable)[$(focusable).index(ob) + 1]).is('[readonly]') ||
		typeof ($($(focusable)[$(focusable).index(ob) + 1])) === undefined
	) {
		// console.log($($(focusable)[$(focusable).index(ob) + 1]));
		return searchFocusable($($(focusable)[$(focusable).index(ob) + 1]));
	} else {
		// console.log($($(focusable)[$(focusable).index(ob) + 1]));
		return $($(focusable)[$(focusable).index(ob) + 1]);
	}
}

var extractText = function (node) {
	var v = $(node).find('span').text() || $(node).find('.form-control').val();
	return v;
}

function paramToJSON(params) {
	var pob = '{';
	params = params.split(',');
	$.each(params, function (i, el) {
		pob += "\"" + el.split('_')[0] + "\"" + ':' + el.split('_')[1] + ',';
	});
	pob = pob.slice(0, -1) + '}';
	return $.parseJSON(pob);
}

function createColumn(ob, c) {
	"use strict";
	$('.date').datepicker('destroy');
	$(ob + ' thead tr th:last-child').each(function (i, el) {
		$(el).css('display', 'table-cell');
		cloneEl(el, c);
		var newCol = $(ob + ' thead tr th:last-child');
		$(newCol).find('span').text($(newCol).find('span').text().split(' ')[0] + ' ' + c);
	});
	$(ob + ' tbody tr td:last-child').each(function (i, el) {
		$(el).css('display', 'table-cell');
		cloneEl(el, c);
	});
	$(ob + ' tfoot tr th:last-child').each(function (i, el) {
		$(el).css('display', 'table-cell');
		cloneEl(el, c);
		var newCol = $(ob + ' tfoot tr th:last-child');
		$(newCol).find('span').attr('id', $(newCol).find('span').attr('id').split('_')[0] + '_' + c).text('');
	});

	splitData(ob);
}

function cloneEl(el, c) {
	"use strict";
	var col = $(el).clone(true);
	var ctrl = $(col).find('.form-control').val('');

	$(col).find('.date').datepicker("destroy").removeClass('hasDatepicker');
	$(ctrl).each(function (i, el) {
		if ($(el).is('[id]')) {
			var id = $(el).attr('id').split('_')[0];
			$(el).attr('id', id + '_' + c);
		}
		if ($(el).is('[type="number"')) {
			$(el).val($(el).attr('min'));
		}
	});
	$(el).parent('tr').append(col);
	$('.date').datepicker();
}

function splitData(ob) {
	"use strict";
	$(ob + ' tbody tr').each(function (r, el) {
		var n = $($(el).find('td input[type="number"]:enabled')).length,
			tgt = $($(el).find('td input[type="number"]:first')).val(),
			avg = parseInt(tgt / n),
			total = 0, bal;
		bal = tgt - (avg * n);
		$($(el).find('td input[type="number"]:enabled')).each(function (i, data) {
			$(data).val(avg);
			total += avg;
			//console.log(' Total: ' + total + '\n tgt: ' + tgt);
		});
		$(el).find('td input[type="number"]:last').val(avg + bal).blur();
	});
}

function showalert(message, wrapper, alerttype, duration) {
	"use strict";
	duration = (duration <= 0) ? null : duration;
	if ($(wrapper).find('.alert').length > 0) {
		$(wrapper).find('.alert').removeClass('in bounce').addClass('slideUpOut').remove()
	}
	if ($(wrapper).find('.alert').length <= 0) {
		$(wrapper).html('<div class="alert alert-' + alerttype + ' alert-dismissible" role="alert"><button type="button" class="close"><span aria-hidden="true">&times;</span></button>' + message + '</div>');
		$(wrapper).find('.alert').addClass('in bounce');
		if (duration !== null) {
			setTimeout(function () { $(wrapper).find('.alert').trigger('click') }, duration);
		}
		$('.bounce').on('webkitAnimationEnd animationend oAnimationEnd animationend', function () {
			$(this).removeClass('in bounce');
		});
		$(wrapper).find('.alert')[0].onclick = function () {
			$(this).removeClass('in bounce').addClass('slideUpOut');
			//'transitionend webkitTransitionEnd oTransitionEnd MSTransitionEnd'
			$(this).on('webkitAnimationEnd animationend oAnimationEnd animationend', function () {
				$(this).off();
				$(this).remove();
			});
		}
	} else {
		$(wrapper).addClass('in bounce');
	}
}

function closeAlert(alertWrapper, duration) {
	"use strict";
	setTimeout(function () {
		// this will automatically close the alert and remove this if the users doesnt close it in {{duration}} Milli Secs
		$(alertWrapper).fadeOut(500);
	}, duration);
}

/**!
* Owl Carousel with Data Attributes
* @author: Ajith S Punalur
* @version: 2.0
**/
var carousel = {
	init: function () {
		"use strict";
		$('[data-carousel="owl"]').each(function (i, el) {
			// var mgn = ($(el).data('margin') !== undefined)? parseInt($(el).data('margin')): 10;
			// console.log($('html').attr('dir')==='rtl')
			var nav = ($(el).data('nav') !== undefined) ?
				$(el).data('nav').split('{{,}}')
				: ["<i class='fa fa-angle-left'></i>", "<i class='fa fa-angle-right'></i>"];
			$(el).owlCarousel({
				// dotsData: true,
				singleItem: true,
				rtl: ($('html').attr('dir') === 'rtl') ? true : false,
				stagePadding: $(el).data('stagePadding') || 0,
				smartSpeed: $(el).data('duration') || 1000,
				animateOut: $(el).data('animateOut') || false,
				animateIn: $(el).data('animateIn') || false,
				items: $(el).data('items') || 3,
				loop: $(el).data('loop') || true,
				dots: $(el).data('dots') || false,
				nav: $(el).data('arrows') || false,
				margin: ($(el).data('margin') === undefined) ? 5 : parseInt($(el).data('margin')),
				center: ($(el).data('center') === undefined) ? false : getBoolean($(el).data('center')),
				autoplay: ($(el).data('autoplay') === undefined) ? true : getBoolean($(el).data('autoplay')),
				autoplayTimeout: $(el).data('timeout') || 1000,
				autoplayHoverPause: $(el).data('pause') || false,
				navText: nav,
				responsive: {
					0: {
						stagePadding: $(el).data('xsStagePadding') || 0,
						items: $(el).data('xsItems') || 1
					},
					480: {
						stagePadding: $(el).data('smStagePadding') || 0,
						items: $(el).data('smItems') || 1
					},
					991: {
						stagePadding: $(el).data('mdStagePadding') || $(el).data('stagePadding') || 0,
						items: $(el).data('mdItems') || $(el).data('items') || 3
					},
					1200: {
						stagePadding: $(el).data('stagePadding') || 0,
						items: $(el).data('items') || 3
					}
				},
				video: true
			});
		});
	},
	forceRefresh: function () {
		"use strict";
		window.dispatchEvent(new Event('resize'));
	}
};

/**!
 * POPUP Javascript
 * @version 3.1.2
 * @author Ajith S Punalur
 * @method popup.init()
 * @method popup.open()
 * @method popup.refresh()
 * @method popup.setData()
 * @method popup.close()
 * @method popup.loader()
 * @date 07-02-2019
 */
var popup = {
	TOP: 0,
	LEFT: 0,
	LEVEL: 0,
	WIDTH: 50,
	HEIGHT: 50,
	CC: 'none invisible inlineMock',
	_error: { message: 'No Errors' },
	/**
	 * @see popup.init width, height, top, left
	 */
	init: function (w, h, t, l) {
		"use strict";
		// Dependency Check
		this._error.message = 'No Errors';
		try {
			if (animation === undefined) { };
			if (TRUTH === undefined) { };
			if (NULL === undefined) { };
			if (kb === undefined) { };
			if (isNull === undefined) { };
			if (getBoolean === undefined) { };
			if (onlyUnique === undefined) { };
			if (getRandomInt === undefined) { };
			if (abs === undefined) { };
		} catch (err) {
			// console.log(err);
			this._error.message = err.message + "\n Include the dependancy it and retry...!";
			console.error(this._error.message);
			return;
		}
		$('[data-popup]').each(function (i, el) {
			$(this).attr('tabindex', 0);
		});
		$('[data-modal].popup').each(function (i, el) {
			var dataset = $(el).data(),
				ovClass = '',
				toggleclasses = " none invisible inlineMock ";
			dataset.level = parseInt(dataset.level) || 0;

			popup.LEVEL = (dataset.level !== undefined && typeof (dataset.level) === 'number') ?
				parseInt(dataset.level, 10) : 0;

			if (dataset.block === false) {
				ovClass = 'none';
			} else if (dataset.block === "invisible") {
				ovClass = 'invisible';
			} else {
				ovClass = '';
				if ($(el).closest('.modalOverlay').length > 0) {
					$(el).closest('.modalOverlay').removeClass(toggleclasses + ' ' + dataset.oClass + ' ' + popup.CC);
				}
			}
			if ($(el).closest('.modalOverlay').length < 1) {
				$(el).wrap('<div class="modalOverlay ' + ovClass + '">&nbsp;</div>');
			}
		});

		w = w || 50;
		h = h || 50;
		t = t || 0;
		l = l || 0;

		$('[data-popup]').on('click', function (e) {
			var thisOb = ($(this).is('[data-popup]')) ? this : $(this).closest('[data-popup]'),
				dataset = $(thisOb).data(),
				modal = $(thisOb).data('popup'),
				ob = '[data-modal=' + modal + '].popup:nth-of-type(1)';
			if ($.trim(modal) === '') { return; }
			dataset.offsetX = !(dataset.hasOwnProperty("offsetX")) ? 0 : dataset.offsetX;
			dataset.offsetY = !(dataset.hasOwnProperty("offsetY")) ? 0 : dataset.offsetY;

			$(thisOb).attr('data-offset-x', dataset.offsetX);
			$(thisOb).attr('data-offset-y', dataset.offsetY);

			w = $(thisOb).data('width') || 50;
			h = $(thisOb).data('height') || 50;
			t = $(thisOb).data('top') || h / 2;
			l = $(thisOb).data('left') || w / 2;

			if (dataset.position === 'inline' || $(ob).attr('data-position') === 'inline') {
				var rel = dataset.relate;

				if ($(rel).length > 0) {
					t = $(rel).offset().top + dataset.offsetY;
					l = $(rel).offset().left + dataset.offsetX;
					// console.log(t,l);
				} else {
					// DONT DELETE FOLLOWING COMMENTS > Needs to Check
					t = (e.pageY - e.offsetY) + $(this).innerHeight() + dataset.offsetY;
					l = (e.pageX - e.offsetX) + dataset.offsetX;
					// t = $(this).offset().top + dataset.offsetY;
					// l = $(this).offset().left + dataset.offsetX;
				}

				$(ob).attr({
					'data-top': t,
					'data-left': l
				});

				// console.log(t,l)
				// console.log(e);
				popup.TOP = t;
				popup.LEFT = l;

				var ww = $(document).width(),
					wh = $(document).height();
				// popup.LEFT = ((l + w) > $(window).width())? abs(l - w) + e.offsetX: l;
				// console.log("POPUP{\n left: %s,\n width: %s}\nWINDOW{width: %s}", l, w, ww);
				popup.TOP = ((t + h) > wh) ? t - ((t + h) - wh) : t;
				popup.LEFT = ((l + w) > ww) ? l - ((l + w) - ww) : l;
				// popup.WIDTH = w;
				// popup.HEIGHT = h;
				// console.log('init >> %d X %d @ X=%d and Y=%d', w, h, l, t);
			}

			if (dataset.units === 'px' || dataset.units === 'pixel') {
				$(ob).attr('data-units', 'px');
				t = $(thisOb).data('top') || 0;
				l = $(thisOb).data('left') || 0;
				w = $(thisOb).data('width') || $(window).width() - 20;
				h = $(thisOb).data('height') || $(window).height() - 20;
				//console.log(w)
			} else {
				//console.log('init : %')
				$(ob).attr('data-units', '%');
				w = (w >= 100) ? 100 : w;
				h = (h >= 100) ? 100 : h;
				t = (t >= h / 2) ? 0 : t;
				l = (l >= w / 2) ? 0 : l;
			}
			//console.log($(ob).attr('data-units'));
			if ($(this).is('[data-xpopup="true"]')) {
				parent.popup.open(ob, w, h, dataset, t, l);
			} else {
				popup.open(ob, w, h, dataset, t, l);
			}
		});

		$('.popup [data-hide]').off('click.popup').on('click.popup', function () {
			popup.close($(this).closest('.popup'));
		});
		$('[data-modal].popup').each(function (i, el) {
			$(el).trigger("popup.init", $(el));
		});
	},
	open: function (obPop, w, h) {
		"use strict";
		var vArgs = Array.prototype.slice.call(arguments, 3),
			t = Array.prototype.slice.call(arguments, 4, 5),
			l = Array.prototype.slice.call(arguments, 5),
			i = 0,
			ob,
			key,
			tgt,
			dataset = vArgs[0] || $(obPop).data(),
			video = $(obPop).find('video'),
			ovClass = '',
			// toggleclasses = "none invisible inlineMock",
			modal = $(obPop).data('modal');

		ob = obPop || '[data-modal=' + modal + '].popup:nth-of-type(1)';
		dataset.class = dataset.class || '';
		dataset.oClass = dataset.oClass || '';
		dataset.level = parseInt(dataset.level, 10) || 0;
		dataset.animateIn = dataset.animateIn || '';
		dataset.animateOut = dataset.animateOut || '';
		dataset.block = dataset.block || true;

		// console.log(dataset.level);

		popup.LEVEL = (dataset.level !== NaN && typeof (dataset.level) === 'number') ?
			parseInt(dataset.level, 10) : 0;

		if (dataset.block === false) {
			ovClass = 'none';
		} else if (dataset.block === "invisible") {
			ovClass = 'invisible';
		} else {
			ovClass = '';
		}

		$(obPop).closest('.modalOverlay').removeClass(ovClass + ' ' + dataset.oClass + ' ' + popup.CC);

		$(obPop).closest('.modalOverlay').addClass(ovClass + ' ' + dataset.oClass);

		// console.log(dataset);
		dataset.offsetX = !(dataset.hasOwnProperty("offsetX")) ? 0 : parseInt(dataset.offsetX, 10);
		dataset.offsetY = !(dataset.hasOwnProperty("offsetY")) ? 0 : parseInt(dataset.offsetY, 10);

		$(obPop).attr('data-offset-x', dataset.offsetX);
		$(obPop).attr('data-offset-y', dataset.offsetY);
		for (i = 0; i < vArgs.length; i += 1) {
			// t = vArgs[i]
			for (key in vArgs[i]) {
				// console.log(key)
				if (vArgs[i].hasOwnProperty(key)) {
					if (key.match(/^xsource/)) {
						// console.log('xs');
						$(obPop).attr('data-xsource', true);
						$(obPop).find('.popContent').html('<iframe id="' + $(obPop).attr('id') + '_iframe" onload="popup.loader(\'' + obPop + '\',false)" src="' + vArgs[i][key] + '" frameborder="0"></iframe>');
					}
					// will be DEPRECATED on future versions
					else if (key.match(/^setAttr/)) {
						tgt = key.replace(/^setAttr/, '').toLowerCase();
						//console.log(key + ' >>> ' + tgt);
						$(obPop).find('[data-get-attr-' + tgt + ']').attr(tgt, vArgs[i][key]);
					} else if (key.match(/^setData/)) {
						tgt = key.replace(/^setData/, '').toLowerCase();
						// console.log(tgt, $(obPop).find('[data-get-data-' + tgt + ']'));
						// console.log(key + ' >>> ' + tgt);
						$(obPop).find('[data-get-data-' + tgt + ']').attr('data-' + tgt, vArgs[i][key]);
					} else if (key.match(/^setText/)) {
						$(obPop).find('[data-get-text]').text(vArgs[i][key]);
					} else if (key.match(/^setTemplate/)) {
						$(obPop).find('[data-get-template]').html(vArgs[i][key]);
					}
					// --ENDS will be DEPRECATED on future versions
					else if (key.match(/^set/)) {
						// tgt = key.replace(/^set/, '').toLowerCase();
						// console.log(dataset[key]);
						try {
							var ds = $.parseJSON(dataset[key].replace(/'/g, '"'));
						} catch (e) {
							// error
							if (typeof (dataset[key]) === 'object') {
								ds = dataset[key];
							} else if (typeof (dataset[key]) === 'string') {
								console.error('SYNTAX Error: Datatype Mismatch / Incorrect Data Format. \n ::: %s', dataset[key]);
								return;
							}
						}

						if (ds.datatype === 'JSON_encoded' && typeof ds.data === 'string') {
							if ($(ds.data).length > 0) {
								var json = hardDecode($(ds.data).val());
								// console.log(json);
								this.setData(obPop, json);
							} else {
								console.error('EXCEPTION: Datasource not found.');
							}
						} else if (ds.datatype === 'JSON_string') {
							var json = ds.data;
							// console.log(json);
							this.setData(obPop, json);
						} else if (ds.datatype === 'JSON_object' || typeof ds.data === 'object') {
							var json = ds.data;
							// console.log(json);
							this.setData(obPop, json);
						} else {
							console.error(json);
						}
					}
				}
			}
		}

		if (video) {
			$(video).each(function (i, el) {
				if ($(el).data('autoplay') === true) {
					el.play();
				}
			});
		}
		// (Called on document.ready) popup.activate();
		if (dataset.units === 'px' || dataset.units === 'pixel') {
			//console.log('open : px')
			w = w || $(window).width() - 20;
			h = h || $(window).height() - 20;

			if (!(dataset.position === 'inline') || getBoolean(dataset.responsive) === true) {
				w = (w >= $(window).width()) ? $(window).width() : w;
				h = (h >= $(window).height()) ? $(window).height() : h;
			}

			if (dataset.position === 'center') {
				t = (abs($(window).height() - h) / 2);
				l = (abs($(window).width() - w) / 2);
			} else if (dataset.position === 'absolute') {
				t = (dataset.top > ($(window).height() - h)) ? $(window).height() - h : dataset.top;
				l = (dataset.left > ($(window).width() - w)) ? $(window).width() - w : dataset.left;
			} else if (dataset.position === 'inline') {
				var rel = dataset.relate;
				// console.log(rel);
				var ww = $(document).width(),
					wh = $(document).height();
				if ($(rel).length > 0) {
					// console.log(dataset.offsetX);
					t = $(rel).offset().top + dataset.offsetY;
					l = $(rel).offset().left + dataset.offsetX;
					// console.log(t,l);
					// console.log("POPUP{\n left: %s,\n width: %s}\nWINDOW{width: %s}", l, w, ww);

					l = ((l + w) > ww) ? l - ((l + w) - ww) : l;
					t = ((t + h) > wh) ? t - ((t + h) - wh) : t;
				} else {
					t = popup.TOP;
					l = popup.LEFT;
					l = ((l + w) > ww) ? l - ((l + w) - ww) : l;
					t = ((t + h) > wh) ? t - ((t + h) - wh) : t;

					l = (l < 0) ? 0 : l;
					t = (t < 0) ? 0 : t;

					// DONT DELETE FOLLOWING COMMENTS > Needs to Check
					// t = $(this).offset().top + dataset.offsetY;
					// l = $(this).offset().left + dataset.offsetX;
				}

				$('body').css('position', 'relative');
				$(obPop).closest('.modalOverlay').addClass('inlineMock');
				popup.TOP = t;
				popup.LEFT = l;
			}
			$(ob).attr({
				'data-units': 'px',
				'data-width': w,
				'data-height': h,
				'data-class': dataset.class,
				'data-relate': dataset.relate,
				'data-top': t + dataset.offsetY,
				'data-left': l + dataset.offsetX,
				'data-offset-x': dataset.offsetX,
				'data-offset-y': dataset.offsetY,
				'data-animate-in': dataset.animateIn,
				'data-animate-out': dataset.animateOut,
				'data-position': dataset.position || 'center'
			});
		} else {
			//console.log('open : %')
			w = w || 50;
			h = h || 50;

			w = (w >= 100) ? 100 : w;
			h = (h >= 100) ? 100 : h;

			$(ob).attr({
				'data-top': t,
				'data-left': l,
				'data-width': w,
				'data-height': h,
				'data-units': '%',
				'data-class': dataset.class,
				'data-animate-in': dataset.animateIn,
				'data-animate-out': dataset.animateOut,
				'data-position': dataset.position || 'center'
			});
		}

		popup.TOP = t;
		popup.LEFT = l;
		popup.WIDTH = w;
		popup.HEIGHT = h;

		//console.log(modal +" "+ w  +" "+ h);
		if (dataset.block === true || dataset.block === undefined || dataset.block === 'invisible') {
			$('body').addClass('modalOpen');
		}
		if ((dataset.scroll === true || dataset.position === 'inline') && (dataset.block === true || dataset.block === undefined)) {
			$('body').removeClass('modalOpen');
		}

		if ($.trim(dataset.animateIn) !== '') {
			$(ob).addClass('open ' + dataset.class + ' ' + dataset.animateIn);
			$(ob).one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function (e) {
				if ($(e.target).is('.popup')) {
					$(ob).removeClass(dataset.animateIn);
					popup.refresh();
				}
			}).parent('.modalOverlay').addClass('active ' + dataset.oClass).css({
				// 'display': 'block',
				// 'visibility': 'visible',
				'z-index': 1050 + dataset.level
			});
		} else {
			$(ob).addClass('open ' + dataset.class).parent('.modalOverlay').addClass('active ' + dataset.oClass).css({
				// 'display': 'block',
				// 'visibility': 'visible',
				'z-index': 1050 + dataset.level
			});
		}

		if (dataset.units === 'px' || dataset.units === 'pixel') {
			$(ob).attr('data-units', 'px');
			// console.log(t)
			var mw = ((abs(l) + w) > $(window).width()) ? 1 : abs(l);
			// console.log(mw);
			$(ob).css({
				'top': t + 'px',
				'left': l + 'px', // (abs($(window).width() - w) / 2) + 'px',
				'width': w + 'px',
				'height': h + 'px',
				'max-width': 'calc(100% - ' + mw + 'px)'
			});
		} else {
			$(ob).attr('data-units', '%');
			$(ob).css({
				'top': (abs(100 - h) / 2) + '%',
				'left': (abs(100 - w) / 2) + '%',
				'width': w + '%',
				'height': h + '%'
			});
		}

		dataset.top = popup.TOP;
		dataset.left = popup.LEFT;
		dataset.width = popup.WIDTH;
		dataset.height = popup.HEIGHT;

		$(ob)[0]._config = dataset;

		// console.log('after Open:  ' + $(ob).attr('data-units'));
		popup.refresh(ob);

		$(document).trigger($.Event('popup.open'), $(obPop));
		$(obPop).trigger($.Event('popup.open'), $(obPop));
		// $(obPop).on('popup.open', function () {
		//     $(obPop).attr('class');
		// });
	},
	refresh: function () {
		"use strict";
		var ob = '.popup.open', dataset, hdr, ftr;

		$(ob).each(function (i, el) {
			dataset = $(el)[0].dataset;
			// console.log(dataset);
			popup.WIDTH = parseInt(dataset.width, 10);
			popup.HEIGHT = parseInt(dataset.height, 10);
			popup.TOP = parseInt(dataset.top, 10) || 0;
			popup.LEFT = parseInt(dataset.left, 10) || 0;

			$('.popup [data-hide]').off('click.popup').on('click.popup', function () {
				popup.close($(this).closest('.popup'));
			});

			$('.modalOverlay').off('click.popup').on('click.popup', function (e) {
				// e.stopPropagation();
				// console.log(e.target);
				if ($(e.target).is('.modalOverlay') && $(e.target).find('[data-dismiss="true"].popup').length > 0) {
					popup.close($(this).find('.popup'));
				}
			});

			$(document).off('keydown.popup').on('keydown.popup', function (e) {
				if (e.which === kb.ESC) {
					$('.modalOverlay.active').each(function (i, el) {
						if ($(el).find('.popup[data-dismiss="true"]').length > 0) {
							popup.close($(this).find('.popup'));
						}
					});
				}
			});

			if (dataset.units === 'px' || dataset.units === 'pixel') {
				$(el).attr('data-units', 'px');
				var w = (popup.WIDTH >= $(window).width()) ? $(window).width() : popup.WIDTH,
					h = (popup.HEIGHT >= $(window).height()) ? $(window).height() : popup.HEIGHT,
					t = popup.TOP,
					l = popup.LEFT;

				if (dataset.position === 'center') {
					t = (abs($(window).height() - h) / 2);
					l = (abs($(window).width() - w) / 2);
				} else if (dataset.position === 'absolute') {
					t = (dataset.top > ($(window).height() - h)) ? $(window).height() - h : dataset.top;
					l = (dataset.left > ($(window).width() - w)) ? $(window).width() - w : dataset.left;
				} else if (dataset.position === 'inline') {
					$('body').css('position', 'relative');
					$(el).closest('.modalOverlay').addClass('inlineMock');
					t = popup.TOP;
					l = popup.LEFT;
					w = popup.WIDTH;
					h = popup.HEIGHT;

					l = (l < 0) ? 0 : l;
					t = (t < 0) ? 0 : t;
					// l = ((l + w) > $(window).width())? abs(l - w): l;
				}
				// console.log(t);
				// t = (popup.TOP >= $(window).height()/2) ? 0 : (abs($(window).height() - h) / 2) + 'px',
				// l = (popup.LEFT >= $(window).width()/2) ? 0 : (abs($(window).width() - w) / 2) + 'px';
				// console.log('Refresh : px', popup.WIDTH, $(window).width(), w);

				$(el).css({
					'top': t,
					'left': l,
					'width': w + 'px',
					'height': h + 'px'
				});
			} else {
				// console.log('refresh : %');
				$(el).attr('data-units', '%');
			}

			hdr = $(el).find('.popHeader').outerHeight();
			ftr = $(el).find('.popFooter').outerHeight();
			$(el).find('.popContent').css('height', $(el).outerHeight() - (hdr + ftr));
			$(el).trigger("popup.refresh", $(el));
		});
	},
	setData: function (obPop, json) {
		if (typeof json === "string" && json !== '') {
			json = JSON.parse(json);
		} else if (typeof json !== "object") {
			console.error("Invalid Format JSON: ", json);
		}
		// console.log(obPop, json);
		$(json).each(function (di, dx) {
			// for (var k in dx) {
			var k = 'selector';
			if (dx.hasOwnProperty(k)) {
				// console.log(k, dx[k]);
				if ($(obPop).find(dx[k]).length <= 0) {
					return;
				}
				if (dx.hasOwnProperty('attr')) {
					$(obPop).find(dx[k]).attr(dx['attr']);
				}
				if (dx.hasOwnProperty('removeAttr')) {
					$(obPop).find(dx[k]).removeAttr(dx['removeAttr']);
				}
				if (dx.hasOwnProperty('class')) {
					$(obPop).find(dx[k]).addClass(dx['class']);
				}
				if (dx.hasOwnProperty('removeClass')) {
					$(obPop).find(dx[k]).removeClass(dx['removeClass']);
				}
				if (dx.hasOwnProperty('val')) {
					if ($(dx[k]).is('input:checkbox') || $(dx[k]).is('input:radio')) {
						$(obPop).find(dx[k])[0].checked = getBoolean(dx['val']);
					} else if ($(dx[k]).is('[data-range-slider]')) {
						$(dx[k]).slider('value', dx['val']);
					} else {
						$(obPop).find(dx[k]).val(dx['val']).trigger('keyup');
					}
				}
				if (dx.hasOwnProperty('text')) { // k === 'selector'
					$(obPop).find(dx[k]).text(dx['text']);
				}
				if (dx.hasOwnProperty('template')) { // k === 'selector'
					$(obPop).find(dx[k]).html(dx['template']);
				}
				if (dx.hasOwnProperty('html')) { // k === 'selector'
					$(obPop).find(dx[k]).html(dx['html']);
				}
			}
			// }
		});
	},
	close: function (obPop) {
		"use strict";
		var reset = $(obPop).find('[data-reset="true"]'),
			dataset = $(obPop)[0]._config || $(obPop)[0].dataset;
		if (reset.length > 0) {
			$(reset).each(function (i, e) {
				switch (e.tagName.toLowerCase()) {
					case 'video':
						e.load();
						break;
					case 'iframe':
						e.src = '';
						break;
					case 'input':
					case 'select':
					case 'textarea':
						if ($(e).attr('type') === 'radio' || $(e).attr('type') === 'checkbox') {
							if ($(e).is('[data-init]')) {
								e.checked = $(e).attr('data-init');
							} else {
								e.checked = false;
							}
						} else {
							e.value = '';
						}
						break;
					default:
						$(obPop).find(e).html('');
						// console.log(e.tagName.toLowerCase());
						break;
				}
			});
		}

		$('body').removeClass('modalOpen');

		if ($.trim(dataset.animateOut) !== '') {
			$(obPop).addClass(dataset.animateOut).one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function () {
				$(this).removeClass('open ' + dataset.class + ' ' + dataset.animateOut)
					.parent('.modalOverlay').removeClass('active' + ' ' + dataset.oClass + ' ' + popup.CC).removeAttr('style');
			});
			popup.refresh();
		} else {
			$(obPop).removeClass('open ' + dataset.class).closest('.modalOverlay').removeClass('active' + ' ' + dataset.oClass + ' ' + popup.CC).removeAttr('style');
		}

		$(document).trigger("popup.close", $(obPop));
		$(obPop).trigger("popup.close", $(obPop));
	},
	loader: function (obPop, opt) {
		"use strict";
		$(obPop).attr('data-loader', opt);
	}
};
$(function () { "use strict"; popup.init(); });
$(window).resize(function () { "use strict"; popup.refresh(); });

/**!
Dialog Box Javascript
* @version 1.0.3
*/
var dialog = {
	init: function (ob, data) {
		if ($(ob).closest('.dialogBackdrop').length < 1) {
			$(ob).wrap('<div class="dialogBackdrop ">&nbsp;</div>');
		}
		// if (data.backdrop === true) {
		// }
	},
	open: function (ob, data) {
		$(ob).find('.dialog-message').html(data.message);
		$('.dialog-footer').find('button').remove();
		$('.dialog-footer').html('');

		$.find(data.buttons).each(function (i, button) {
			var btn = $('<button/>');
			button.class = (button.class === undefined) ? 'btn btn-mtl btn-link ' : 'btn btn-mtl btn-link ' + button.class;
			console.log(button.class)
			$(btn).attr('id', 'dialogBtn-' + i).addClass(button.class).html(button.label);
			$(ob).find('.dialog-footer').append(btn);

			$(ob).find('#dialogBtn-' + i).off('click.dialog.button_i').on('click.dialog.button_i', function () {
				if (button.action !== undefined && typeof button.action === "string") {
					console.log(button.action);
					var fnName = button.action.split('('),
						fnparams = fnName[1].split(')')[0].split(','),
						fn = window[fnName[0]];
					if (typeof fn === "function") fn.apply(null, fnparams);
				} else if (typeof button.action === "function") {
					button.action.apply(null, fnparams)
				} else {
					console.error("Undefined Error , %o", button.action);
				}
			});
		});

		if (data.backdrop === true) {
			$(ob).closest('.dialogBackdrop').addClass('active');
		} else {
			$(ob).closest('.dialogBackdrop').addClass('transparent');
		}
		$(ob).css({
			"width": data.width,
			"height": data.height
		}).addClass('open').removeAttr('open');

	},
	close: function (ob, data) {
		$(ob).removeClass('open');
		$(ob).removeAttr('open');
		if (data.backdrop === true) {
			$(ob).closest('.dialogBackdrop').removeClass('active');
		}
	}
};

/**
* @param duplicator.init()
* @param duplicator.createClone(element, params, formControl)
* @param duplicator.resetCtrls(formControl)
* @version: 1.b.0
*/
var duplicator = {
	init: function () {
		var d = duplicator;
		$('.clone.btn').off('click.duplicator').on('click.duplicator', function () {
			var params = $.escapeSelector($(this).attr('data-params')),
				foc = $.escapeSelector($(this).attr('data-focus')),
				row = $(this).closest('[data-current]').clone(true),
				fiEvent = $.Event('duplicator.row.creating'),
				dataRow = $(this).closest('[data-parent]').attr('data-parent');

			$(document).trigger(fiEvent, [$(this).closest('[data-current]')]);
			if (fiEvent.isDefaultPrevented() === true) { return true; }

			foc = foc.split('-')[0];
			params = paramToJSON(params);
			params.r = params.r + 1;
			row = d.createClone(row, params);

			$(row).find('.clone.btn').attr('data-params', 'c_' + params.c + ',r_' + (params.r));
			$(this).closest('.dataRowCards').find('.clone.newCard').attr('data-params', 'c_' + params.c + ',r_' + (params.r));

			$(this).closest('[data-parent] > .dataRows').append(row);

			page.refresh();

			$(this).closest('[data-current]').attr('data-last', "false");
			$(this).remove();
			$('#' + foc + '-' + params.c + '_r' + (params.r)).focus();

			fiEvent = $.Event('duplicator.row.created');
			$(document).trigger(fiEvent, [row]);
		});

		$('.destroy.btn').off('click.duplicator').on('click.duplicator', function () {
			var l = $(this).closest('.dataRows').find('[data-last]').length,
				fiEvent = $.Event('duplicator.row.removing'),
				dataRow = $(this).closest('[data-parent]').attr('data-parent');

			$(document).trigger(fiEvent, [$(this).closest('[data-current]')]);
			if (fiEvent.isDefaultPrevented() === true) { return true; }

			if (l > 1) {
				// console.log('More than one Row');
				if (($(this).closest('[data-current]').is('[data-last="true"]'))) {
					// console.log('LAST Row');
					var addBtn = $(this).prev('.btn.clone').clone(true);
					$(this).closest('[data-current]').prev(dataRow).find('[data-focus^=clone]').attr('data-focus', $(addBtn).attr('id'));
					$(this).closest('[data-current]').prev(dataRow).attr('data-last', 'true').find('.cloneController').prepend(addBtn);
				}
				$(this).closest('[data-current]').remove();
			} else {
				// console.log('One Row Only: Deletion prevented')
				$(this).closest('[data-current]').find('[data-control]').each(function (idx, ctrl) {
					duplicator.resetCtrls(ctrl);
				});
			}

			fiEvent = $.Event('duplicator.row.removed');
			$(document).trigger(fiEvent);
		});

		$('.newCard.clone').off('click.duplicator').on('click.duplicator', function (e) {
			var params = $.escapeSelector($(this).attr('data-params')),
				card = $(this).closest('[data-current]').clone(true),
				fiEvent = $.Event('duplicator.card.creating');

			$(document).trigger(fiEvent, [$(this).closest('[data-current]')]);
			if (fiEvent.isDefaultPrevented() === true) { return true; }

			params = paramToJSON(params);
			params.r = 1;
			params.c = params.c + 1;
			card = d.createClone(card, params);

			$(card).find('[data-last="false"]').remove();
			$(card).attr('data-current', params.c + ',' + params.r);
			$(card).find('[data-current]').attr('data-current', params.c + ',' + params.r);
			$(card).find('.clone.newCard').attr('data-params', 'c_' + params.c + ',r_' + params.r);
			$(card).find('.clone.btn').attr('data-params', 'c_' + params.c + ',r_' + params.r);
			$(this).closest('.dataRowCards').append(card);
			// .json2html({
			//   'cID': params.c + 1,
			//   'rID': params.r
			// }, cardTransform);
			//$('#jobCard_' + (params.c + 1) )
			//.json2html(data,transform);
			page.refresh();

			// $(this).closest('[data-current].card').find('.clone.newCard').attr('data-params', 'c_' + params.c + ',r_' + params.r);
			// $(this).closest('[data-current].card').find('.clone.btn').attr('data-params', 'c_' + params.c + ',r_' + params.r);
			$(this).remove();

			fiEvent = $.Event('duplicator.card.created');
			$(document).trigger(fiEvent, card);
		});
		$('.thisCard.destroy').off('click.duplicator').on('click.duplicator', function () {
			var l = $(this).closest('.dataRowCards').find('[data-last].card').length,
				fiEvent = $.Event('duplicator.card.removing');
			dataRow = $.escapeSelector($(this).attr('data-parent'));
			$(document).trigger(fiEvent, [$(this).closest('[data-current]')]);
			if (fiEvent.isDefaultPrevented() === true) { return true; }

			if (l > 1) {
				// console.log('More than one Card');
				if (($(this).closest('[data-current]').is('[data-last="true"]'))) {
					// console.log('LAST Card');
					var addBtn = $(this).prev('.newCard.clone').clone(true);
					$(this).closest('[data-current].card').prev('.card').attr('data-last', 'true').find('.btn-fab-group').prepend(addBtn);
				}
				$(this).closest('[data-current]').remove();
			} else {
				// console.log('One Card Only: Deletion prevented')
				$(this).closest('[data-current]').find(dataRow + ':not([data-last="true"])').remove();
			}

			fiEvent = $.Event('duplicator.card.removed');
			$(document).trigger(fiEvent);
		});
	},
	createClone: function (element, params, formControl) {
		formControl = (formControl === undefined || formControl === '') ? '[data-control]' : formControl;
		var ctrl = $(element).find(formControl).val(''),
			idRes = $(element).find('[id]'),
			fiEvent = $.Event('duplicator.duplicating');
		$(element).attr('data-current', params.c + ',' + params.r);
		//$(element).find('.date').datepicker("destroy").removeClass('hasDatepicker');
		$(document).trigger(fiEvent, [element, params]);
		if (fiEvent.isDefaultPrevented() === true) { return true; }

		$(idRes).each(function (i, el) {
			if ($(el).is('[id]')) {
				var id = $(el).attr('id') || '',
					foc = $(el).data('focus') || '',
					regx = /(-[\d]_r[\d]+)/;
				id = id.replace(regx, '-' + params.c + '_r' + (params.r));
				if (typeof foc !== 'undefined') { foc = foc.replace(regx, '-' + params.c + '_r' + params.r); }

				$(this).removeClass('.autoCombo').attr("id", "").removeData().off();

				$(el).attr({
					'id': id,
					'data-focus': foc
				});

				if ($(el).is('[data-type="autocomplete"]')) {
					$(el).addClass('.autoCombo');
				}
			}
		});

		$(ctrl).each(function (i, el) {
			duplicator.resetCtrls(el);
		});
		return element;
	},
	resetCtrls: function (ctrl) {
		if ($(ctrl).is('[data-init]')) {
			if ($(ctrl).attr('data-init').match(/[\[][^\]]*[\]]/)) {
				var v = $(el).attr('data-init');
				$(el).attr('data-init', $(el).closest(v).attr(v.replace(/[\[\]]/g, '')));
			} else if ($(ctrl).is('input[type="radio"]') || $(ctrl).is('input[type="checkbox"]')) {
				$(ctrl).prop("checked", getBoolean($(ctrl).attr('data-init')));
			} else if ($(ctrl).is('input[type="number"]')) {
				if ($(ctrl).is('[min]')) {
					if (
						parseInt($(ctrl).attr('data-init')) >= parseInt($(ctrl).attr('min')) ||
						parseFloat($(ctrl).attr('data-init')) >= parseFloat($(ctrl).attr('min'))
					) {
						$(ctrl).val($(ctrl).attr('data-init'));
					} else {
						$(ctrl).val($(ctrl).attr('min'));
					}
				} else {
					$(ctrl).val($(ctrl).attr('data-init'));
				}
			} else if ($(ctrl).is('select')) {
				$(ctrl).html('<option>' + $(ctrl).attr('data-init') + '</option>');
			} else if ($(ctrl).is(':input')) {
				$(ctrl).val($(ctrl).attr('data-init'));
			} else {
				$(ctrl).html($(ctrl).attr('data-init'));
			}
		} else if ($(ctrl).is(':input')) {
			$(ctrl).val('');
		} else {
			$(ctrl).html('');
		}
	}
};

/**!
* jquery.mask.js
* @version: v1.13.4
* @author: Igor Escobar
* Created by Igor Escobar on 2012-03-10. Please report any bug at http://blog.igorescobar.com
* Copyright (c) 2012 Igor Escobar http://blog.igorescobar.com
* The MIT License (http://www.opensource.org/licenses/mit-license.php)
*/
/* jshint laxbreak: true */
/* global define, jQuery, Zepto */
'use strict';
// UMD (Universal Module Definition) patterns for JavaScript modules that work everywhere.
// https://github.com/umdjs/umd/blob/master/jqueryPluginCommonjs.js
(function (factory) {
	if (typeof define === 'function' && define.amd) {
		define(['jquery'], factory);
	} else if (typeof exports === 'object') {
		module.exports = factory(require('jquery'));
	} else {
		factory(jQuery || Zepto);
	}
}(function ($) {
	var Mask = function (el, mask, options) {
		el = $(el);

		var jMask = this, oldValue = el.val(), regexMask;

		mask = typeof mask === 'function' ? mask(el.val(), undefined, el, options) : mask;

		var p = {
			invalid: [],
			getCaret: function () {
				try {
					var sel,
						pos = 0,
						ctrl = el.get(0),
						dSel = document.selection,
						cSelStart = ctrl.selectionStart;

					// IE Support
					if (dSel && navigator.appVersion.indexOf('MSIE 10') === -1) {
						sel = dSel.createRange();
						sel.moveStart('character', el.is('input') ? -el.val().length : -el.text().length);
						pos = sel.text.length;
					}
					// Firefox support
					else if (cSelStart || cSelStart === '0') {
						pos = cSelStart;
					}

					return pos;
				} catch (e) { }
			},
			setCaret: function (pos) {
				try {
					if (el.is(':focus')) {
						var range, ctrl = el.get(0);

						if (ctrl.setSelectionRange) {
							ctrl.setSelectionRange(pos, pos);
						} else if (ctrl.createTextRange) {
							range = ctrl.createTextRange();
							range.collapse(true);
							range.moveEnd('character', pos);
							range.moveStart('character', pos);
							range.select();
						}
					}
				} catch (e) { }
			},
			events: function () {
				el
					.on('input.mask keyup.mask', p.behaviour)
					.on('paste.mask drop.mask', function () {
						setTimeout(function () {
							el.keydown().keyup();
						}, 100);
					})
					.on('change.mask', function () {
						el.data('changed', true);
					})
					.on('blur.mask', function () {
						if (oldValue !== el.val() && !el.data('changed')) {
							el.triggerHandler('change');
						}
						el.data('changed', false);
					})
					// it's very important that this callback remains in this position
					// otherwhise oldValue it's going to work buggy
					.on('blur.mask', function () {
						oldValue = el.val();
					})
					// select all text on focus
					.on('focus.mask', function (e) {
						if (options.selectOnFocus === true) {
							$(e.target).select();
						}
					})
					// clear the value if it not complete the mask
					.on('focusout.mask', function () {
						if (options.clearIfNotMatch && !regexMask.test(p.val())) {
							p.val('');
						}
					});
			},
			getRegexMask: function () {
				var maskChunks = [], translation, pattern, optional, recursive, oRecursive, r;

				for (var i = 0; i < mask.length; i++) {
					translation = jMask.translation[mask.charAt(i)];

					if (translation) {

						pattern = translation.pattern.toString().replace(/.{1}$|^.{1}/g, '');
						optional = translation.optional;
						recursive = translation.recursive;

						if (recursive) {
							maskChunks.push(mask.charAt(i));
							oRecursive = { digit: mask.charAt(i), pattern: pattern };
						} else {
							maskChunks.push(!optional && !recursive ? pattern : (pattern + '?'));
						}

					} else {
						maskChunks.push(mask.charAt(i).replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&'));
					}
				}

				r = maskChunks.join('');

				if (oRecursive) {
					r = r.replace(new RegExp('(' + oRecursive.digit + '(.*' + oRecursive.digit + ')?)'), '($1)?')
						.replace(new RegExp(oRecursive.digit, 'g'), oRecursive.pattern);
				}

				return new RegExp(r);
			},
			destroyEvents: function () {
				el.off(['input', 'keydown', 'keyup', 'paste', 'drop', 'blur', 'focusout', ''].join('.mask '));
			},
			val: function (v) {
				var isInput = el.is('input'),
					method = isInput ? 'val' : 'text',
					r;

				if (arguments.length > 0) {
					if (el[method]() !== v) {
						el[method](v);
					}
					r = el;
				} else {
					r = el[method]();
				}

				return r;
			},
			getMCharsBeforeCount: function (index, onCleanVal) {
				for (var count = 0, i = 0, maskL = mask.length; i < maskL && i < index; i++) {
					if (!jMask.translation[mask.charAt(i)]) {
						index = onCleanVal ? index + 1 : index;
						count++;
					}
				}
				return count;
			},
			caretPos: function (originalCaretPos, oldLength, newLength, maskDif) {
				var translation = jMask.translation[mask.charAt(Math.min(originalCaretPos - 1, mask.length - 1))];

				return !translation ? p.caretPos(originalCaretPos + 1, oldLength, newLength, maskDif)
					: Math.min(originalCaretPos + newLength - oldLength - maskDif, newLength);
			},
			behaviour: function (e) {
				e = e || window.event;
				p.invalid = [];
				var keyCode = e.keyCode || e.which;
				if ($.inArray(keyCode, jMask.byPassKeys) === -1) {

					var caretPos = p.getCaret(),
						currVal = p.val(),
						currValL = currVal.length,
						changeCaret = caretPos < currValL,
						newVal = p.getMasked(),
						newValL = newVal.length,
						maskDif = p.getMCharsBeforeCount(newValL - 1) - p.getMCharsBeforeCount(currValL - 1);

					p.val(newVal);

					// change caret but avoid CTRL+A
					if (changeCaret && !(keyCode === 65 && e.ctrlKey)) {
						// Avoid adjusting caret on backspace or delete
						if (!(keyCode === 8 || keyCode === 46)) {
							caretPos = p.caretPos(caretPos, currValL, newValL, maskDif);
						}
						p.setCaret(caretPos);
					}

					return p.callbacks(e);
				}
			},
			getMasked: function (skipMaskChars) {
				var buf = [],
					value = p.val(),
					m = 0, maskLen = mask.length,
					v = 0, valLen = value.length,
					offset = 1, addMethod = 'push',
					resetPos = -1,
					lastMaskChar,
					check;

				if (options.reverse) {
					addMethod = 'unshift';
					offset = -1;
					lastMaskChar = 0;
					m = maskLen - 1;
					v = valLen - 1;
					check = function () {
						return m > -1 && v > -1;
					};
				} else {
					lastMaskChar = maskLen - 1;
					check = function () {
						return m < maskLen && v < valLen;
					};
				}

				while (check()) {
					var maskDigit = mask.charAt(m),
						valDigit = value.charAt(v),
						translation = jMask.translation[maskDigit];

					if (translation) {
						if (valDigit.match(translation.pattern)) {
							buf[addMethod](valDigit);
							if (translation.recursive) {
								if (resetPos === -1) {
									resetPos = m;
								} else if (m === lastMaskChar) {
									m = resetPos - offset;
								}

								if (lastMaskChar === resetPos) {
									m -= offset;
								}
							}
							m += offset;
						} else if (translation.optional) {
							m += offset;
							v -= offset;
						} else if (translation.fallback) {
							buf[addMethod](translation.fallback);
							m += offset;
							v -= offset;
						} else {
							p.invalid.push({ p: v, v: valDigit, e: translation.pattern });
						}
						v += offset;
					} else {
						if (!skipMaskChars) {
							buf[addMethod](maskDigit);
						}

						if (valDigit === maskDigit) {
							v += offset;
						}

						m += offset;
					}
				}

				var lastMaskCharDigit = mask.charAt(lastMaskChar);
				if (maskLen === valLen + 1 && !jMask.translation[lastMaskCharDigit]) {
					buf.push(lastMaskCharDigit);
				}

				return buf.join('');
			},
			callbacks: function (e) {
				var val = p.val(),
					changed = val !== oldValue,
					defaultArgs = [val, e, el, options],
					callback = function (name, criteria, args) {
						if (typeof options[name] === 'function' && criteria) {
							options[name].apply(this, args);
						}
					};

				callback('onChange', changed === true, defaultArgs);
				callback('onKeyPress', changed === true, defaultArgs);
				callback('onComplete', val.length === mask.length, defaultArgs);
				callback('onInvalid', p.invalid.length > 0, [val, e, el, p.invalid, options]);
			}
		};


		// public methods
		jMask.mask = mask;
		jMask.options = options;
		jMask.remove = function () {
			var caret = p.getCaret();
			p.destroyEvents();
			p.val(jMask.getCleanVal());
			p.setCaret(caret - p.getMCharsBeforeCount(caret));
			return el;
		};

		// get value without mask
		jMask.getCleanVal = function () {
			return p.getMasked(true);
		};

		jMask.init = function (onlyMask) {
			onlyMask = onlyMask || false;
			options = options || {};

			jMask.byPassKeys = $.jMaskGlobals.byPassKeys;
			jMask.translation = $.jMaskGlobals.translation;

			jMask.translation = $.extend({}, jMask.translation, options.translation);
			jMask = $.extend(true, {}, jMask, options);

			regexMask = p.getRegexMask();

			if (onlyMask === false) {

				if (options.placeholder) {
					el.attr('placeholder', options.placeholder);
				}

				// this is necessary, otherwise if the user submit the form
				// and then press the "back" button, the autocomplete will erase
				// the data. Works fine on IE9+, FF, Opera, Safari.
				if ($('input').length && 'oninput' in $('input')[0] === false && el.attr('autocomplete') === 'on') {
					el.attr('autocomplete', 'off');
				}

				p.destroyEvents();
				p.events();

				var caret = p.getCaret();
				p.val(p.getMasked());
				p.setCaret(caret + p.getMCharsBeforeCount(caret, true));

			} else {
				p.events();
				p.val(p.getMasked());
			}
		};

		jMask.init(!el.is('input'));
	};

	$.maskWatchers = {};
	var HTMLAttributes = function () {
		var input = $(this),
			options = {},
			prefix = 'data-mask-',
			mask = input.attr('data-mask');

		if (input.attr(prefix + 'reverse')) {
			options.reverse = true;
		}

		if (input.attr(prefix + 'clearifnotmatch')) {
			options.clearIfNotMatch = true;
		}

		if (input.attr(prefix + 'selectonfocus') === 'true') {
			options.selectOnFocus = true;
		}

		if (notSameMaskObject(input, mask, options)) {
			return input.data('mask', new Mask(this, mask, options));
		}
	},
		notSameMaskObject = function (field, mask, options) {
			options = options || {};
			var maskObject = $(field).data('mask'),
				stringify = JSON.stringify,
				value = $(field).val() || $(field).text();
			try {
				if (typeof mask === 'function') {
					mask = mask(value);
				}
				return typeof maskObject !== 'object' || stringify(maskObject.options) !== stringify(options) || maskObject.mask !== mask;
			} catch (e) { }
		};


	$.fn.mask = function (mask, options) {
		options = options || {};
		var selector = this.selector,
			globals = $.jMaskGlobals,
			interval = $.jMaskGlobals.watchInterval,
			maskFunction = function () {
				if (notSameMaskObject(this, mask, options)) {
					return $(this).data('mask', new Mask(this, mask, options));
				}
			};

		$(this).each(maskFunction);

		if (selector && selector !== '' && globals.watchInputs) {
			clearInterval($.maskWatchers[selector]);
			$.maskWatchers[selector] = setInterval(function () {
				$(document).find(selector).each(maskFunction);
			}, interval);
		}
		return this;
	};

	$.fn.unmask = function () {
		clearInterval($.maskWatchers[this.selector]);
		delete $.maskWatchers[this.selector];
		return this.each(function () {
			var dataMask = $(this).data('mask');
			if (dataMask) {
				dataMask.remove().removeData('mask');
			}
		});
	};

	$.fn.cleanVal = function () {
		return this.data('mask').getCleanVal();
	};

	$.applyDataMask = function (selector) {
		selector = selector || $.jMaskGlobals.maskElements;
		var $selector = (selector instanceof $) ? selector : $(selector);
		$selector.filter($.jMaskGlobals.dataMaskAttr).each(HTMLAttributes);
	};

	var globals = {
		maskElements: 'input,td,span,div',
		dataMaskAttr: '*[data-mask]',
		dataMask: true,
		watchInterval: 300,
		watchInputs: true,
		watchDataMask: false,
		byPassKeys: [9, 16, 17, 18, 36, 37, 38, 39, 40, 91],
		translation: {
			'0': { pattern: /\d/ },
			'9': { pattern: /\d/, optional: true },
			'#': { pattern: /\d/, recursive: true },
			'A': { pattern: /[a-zA-Z0-9]/ },
			'S': { pattern: /[a-zA-Z]/ }
		}
	};

	$.jMaskGlobals = $.jMaskGlobals || {};
	globals = $.jMaskGlobals = $.extend(true, {}, globals, $.jMaskGlobals);

	// looking for inputs with data-mask attribute
	if (globals.dataMask) { $.applyDataMask(); }

	setInterval(function () {
		if ($.jMaskGlobals.watchDataMask) { $.applyDataMask(); }
	}, globals.watchInterval);
}));

/**!
 * multiAccordionMenu v1.0.1 | (c) 2015 [SDN Technologies] | MIT license
 * @copyright (c) 2015
 * @licence: https://github.com/asphub/multiAccordionMenu/blob/master/LICENSE
 * */
$(function () {
	"use strict"; (function ($) {
		var allMenus = $('.sidebar ul.menu>li').toggleClass('active'); allMenus = $('.sidebar ul.menu>li').toggleClass('active'); $('.sidebar ul.menu>li').hover(function (fe) { $(this).toggleClass('open'); fe.stopPropagation(); }); $('.sidebar ul.menu > li').click(function (fe) { allMenus.removeClass('active'); $('.sidebar ul.menu>li ul>li').removeClass('active'); $(this).toggleClass('active'); fe.stopPropagation(); return true; }); $('.sidebar ul.menu>li>ul>li').click(function (fe) { allMenus.removeClass('active'); $('.sidebar ul.menu>li ul>li').removeClass('active'); $(this).closest('ul').closest('li').addClass('active'); $(this).toggleClass('active'); fe.stopPropagation(); return true; });
		$('.menuToggler').click(function (fe) {
			var w = $(window).width(),
				ob = '.sidebar[data-name="' + $(this).data('toggle') + '"]';
			if ($(this).has($(this).data('toggle')) && w >= 768) {
				$(ob).toggleClass('minimize');
				$(ob).removeClass('close');
			} else {
				$(ob).toggleClass('minimize');
				$(ob).toggleClass('close');
			}
		}); $('*:not(.sidebar)').hover(function (e) { if ($('.open').length > 1) { $('.sidebar ul.menu > li').removeClass('open'); } }); $(window).trigger('resize');
		$(window).on('load resize', function (e) {
			var w = $(window).width();
			if (w < 768) {
				$('.sidebar').addClass('minimize');
			} else {
				$('.sidebar').removeClass('close');
			}
		});
	})(jQuery);
});

(function ($) {
	/**!
	* Material Design JS
	* @version 1.3.1
	* @author Ajith S Punalur
	*/
	$.fn.setVal = function (v) {
		var p = $(this).val(),
			c = Array.prototype.slice.call(arguments, 1);
		$(this).val(v).change();
		if (c.length !== undefined && c.length > 0 && typeof c === "function") {
			c(p, v);
		}
		return this;
	};
	$.fn.getVal = function () {
		return $(this).val();
	};
	$.fn.mtl = function (opt) {
		var State;
		(function (State) {
			State[State["Error"] = 0] = "Error";
			State[State["Success"] = 1] = "Success";
			State[State["Warning"] = 2] = "Warning";
			State[State["Default"] = 3] = "Default";
		})(State || (State = {}));;
		var o = {
			val: $(this).val(),
			message: {
				type: "Error",
				text: ""
			}
		};
		$.extend(o, opt);
		o.message.type = o.message.type.toCapitalCase();
		for (key in o) {
			if (typeof o[key] === 'object') {
				if (key === 'message') {
					$(this).closest('.mtl').removeClass('onError onSuccess onWarning');
					if (o[key]['type'] !== "Default" || o[key]['type'] !== "") {
						$(this).closest('.mtl').addClass('hasMessage on' + o[key]['type']);
						$(this).next('label').find('small').html(o[key]['text']);
					} else {
						$(this).closest('.mtl').removeClass('hasMessage onError onSuccess onWarning');
					}
				}
			} else {
				if (o.hasOwnProperty('attr')) {
					if ($(this).length > 0) {
						if ($.find(o[key]).is('input:checkbox') || $.find(o[key]).is('input:radio')) {
							$(this)[0].checked = true;
						} else if ($.find(o[key]).is('[data-range-slider]')) {
							$(sanitizeText(o[key])).slider('value', o['attr']);
						} else {
							$(this).attr(o['attr']);
						}
					}
				}
				if (o.hasOwnProperty('removeAttr')) {
					$(this).removeAttr(o['removeAttr']);
				}
				if (o.hasOwnProperty('removeClass')) {
					$(this).removeClass(o['removeClass']);
				} else if (o.hasOwnProperty('class')) {
					$(this).addClass(o['class']);
				} else if (o.hasOwnProperty('val')) {
					$(this).val(o['val']).change();
				} else if (o.hasOwnProperty('text')) {
					if ($(this).length > 0) {
						$(this).text(o['text']);
					}
				} else if (o.hasOwnProperty('template')) {
					if ($(this).length > 0) {
						$(this).html(o['template']);
					}
				}
			}
		}

		$(this).materialise({});
	};
	$.fn.materialise = function (custom) {
		var ob = this,
			op = {
				"class": "",
				"message": ""
			},
			mtlClass = 'mtl';

		custom.floatingLabel = (custom.floatingLabel === undefined) ? true : custom.floatingLabel;
		custom.placeholderLabel = (custom.placeholderLabel === undefined) ? true : custom.placeholderLabel;
		custom.validation = (custom.validation === undefined) ? ["", "*"] : custom.validation;

		this.each(function (i, el) {
			var mtlWrap = $(el).parents('.' + mtlClass).length,
				mtl = $(el).parent('.' + mtlClass),
				ds = $(el).data(),
				attr = el.attributes;
			if (elementSupportsAttribute(el.tagName, "id")) {
				(attr.id === undefined) ? el.setAttribute('id', 'mtl-ctrl_' + new Date().getTime()) : attr["id"];
				attr = el.attributes;
			} else {
				attr["id"] = (attr.id === undefined) ? document.createAttribute('id') : attr["id"];
				attr["id"].value = (attr.id.value === '') ? ('mtl-ctrl_' + new Date().getTime()) : attr["id"].value;
			}

			ds.floatingLabel = (ds.floatingLabel === undefined) ? custom.floatingLabel : ds.floatingLabel;
			ds.placeholderLabel = (ds.placeholderLabel === undefined) ? custom.placeholderLabel : ds.placeholderLabel;

			if (ds.validation === undefined) {
				ds.validation = custom.validation;
			} else if (typeof ds.validation !== 'object') {
				ds.validation = ds.validation.split('|');
			}

			var opt = {
				'width': ds.width || '100%',
				'validation': ds.validation,
				"class": ds.class || custom.class,
				'label': ds.label || custom.label,
				'floatingLabel': ds.floatingLabel,
				'message': ds.message || custom.message,
				'placeholderLabel': ds.placeholderLabel,
				'placeholder': ds.placeholder || custom.placeholder,
				'type': ds.type || $(el).attr('type') || $(el).prop("tagName").toLowerCase()
			};

			opt.class = (opt.class === undefined) ? '' : opt.class;
			$.extend(op, opt);

			//init
			if (mtlWrap === 0) {
				$(el).wrap("<div class='" + mtlClass + " " + opt.class + "'></div>");
			} else if (mtlWrap > 1) {
				while (mtlWrap > 1) {
					$($(el).parents('.' + mtlClass)[mtlWrap - 1]).removeClass(mtlClass);
					mtlWrap = $(el).parents('.' + mtlClass).length;
				}
			}

			$(el).closest('.' + mtlClass).addClass(opt.class);

			if ($(el).is(':disabled')) {
				$(el).closest('.' + mtlClass).addClass('disabled');
			}

			//Type of Control
			$(el).parent('.' + mtlClass).addClass(mtlClass + '-' + op.type).attr('data-type', op.type);

			if (getBoolean(op.placeholderLabel) === true && $(el).parent('.' + mtlClass).length === 1) {
				var lbl = $(el).attr('data-placeholder') || $(el).attr('data-label') || $(el).attr('placeholder') || '';
				if (
					lbl !== undefined &&
					$(el).parent('.' + mtlClass).find('.mtl-label').length <= 0
				) {
					if ($.trim(op.validation[0]) !== '') {
						lbl = createElementWithText('i', op.validation[1]).insertAfter(lbl);
					}
					$(el).parent('.' + mtlClass).append(getMatLabel(attr, lbl, op));
					$(el).removeAttr('placeholder').removeAttr('data-label');
				}
			} else if (
				getBoolean(op.placeholderLabel) === false &&
				$(el).parent('.' + mtlClass).length === 1
			) {
				$(el).parent('.' + mtlClass).addClass(mtlClass + '-no-label');
				var lbl = $(el).attr('data-placeholder') || $(el).attr('data-label') || $(el).attr('placeholder') || '';
				if (
					lbl !== undefined &&
					$(el).parent('.' + mtlClass).find('.mtl-label').length <= 0
				) {
					$(el).parent('.' + mtlClass).append(getMatLabel(attr, lbl, op));
				}
			}

			// if (op.placeholder !== '' && op.placeholder !== undefined) {
			// }

			if (getBoolean(op.floatingLabel) === true && getBoolean(op.placeholderLabel) === true) {
				$(el).parent('.' + mtlClass).addClass(mtlClass + '-floatingLabel');
			} else {
				$(el).parent('.' + mtlClass).removeClass(mtlClass + '-floatingLabel');
			}

			// switch ($(el).parent('.' + mtlClass).attr('data-type')) {
			// 	case 'search':
			// 		//$(el).parent('.' + mtlClass).append('<a href="javascript:;" class="btn"><i class="fa fa-search"></i></a>');
			// 		break;
			// 	// default:
			// 	// break;
			// }

			$(el).off('focus.material').on('focus.material',
				function () {
					$(this).closest('.' + mtlClass).addClass('focus');
				});

			$(el)
				.off('change.material input.material click.material blur.material propertychange.material paste.material')
				.on('change.material input.material click.material blur.material propertychange.material paste.material',
					function () {
						if ($.trim($(this).val()) === '') {
							$(this).parent('.' + mtlClass).removeClass('hasValue');
						} else {
							$(this).parent('.' + mtlClass).addClass('hasValue');
						}
					});

			$(el).off('blur.material').on('blur.material', function () {
				$(this).parent('.' + mtlClass).removeClass('focus');
			});

			if ($(el).val() !== '') {
				$(el).parent('.' + mtlClass).addClass('hasValue');
			}
			// $(el).click();
		});
		return this;
	};

	/* alert message */
	$.fn.message = function (data) {
		var alertClass = "alert",
			ob = $(this);
		if ($('.alert.active').length > 0) {
			$('.alert').removeClass('active')
				.removeClass("alert-success alert-warning alert-info alert-danger");
		}
		$(ob).addClass("alert-" + data.type);
		$(ob).find('.' + alertClass + '-message').text(data.message);
		// $(ob).find('.' + alertClass + '-action').text(data.actionText);
		$(ob).addClass('active');
		timeOut = setTimeout(function () {
			$(ob).removeClass('active').removeClass("alert-" + data.type);
			$(ob).find('.' + alertClass + '-message').text('');
			// $(ob).find('.' + alertClass + '-action').text('');
		}, data.timeout);
		$('.' + alertClass + ' .close').on('click', function () {
			clearTimeout(timeOut);
			$(this).parent('.' + alertClass).removeClass('active').removeClass("alert-" + data.type);
			$(this).parent('.' + alertClass).find('.' + alertClass + '-message').text('');
			// $(this).parent('.' + alertClass).find('.' + alertClass + '-action').text('');
		});
		return this;
	};

	/**
	 * @name Toast - Plugin
	 * @version 1.2
	 * @author Ajith S
	 * */
	$.fn.showSnackbar = function (data) {
		var sbClass = "snackbar",
			ob = $(this);

		$.extend(data, {
			class: data.class || "",
			position: data.position || "bottom",
			message: data.message || "",
			actionText: data.actionText || "OK",
			actionHandler: data.actionHandler || void (0)
		});

		$(ob).addClass(data.class);

		if ($('.snackbar.active').length > 0) {
			$('.snackbar').removeClass('active');
		}
		$(ob).attr('data-placement', data.position);
		$(ob).find('.' + sbClass + '-text').text(data.message);
		$(ob).find('.' + sbClass + '-action').text(data.actionText);
		setTimeout(function () {
			$(ob).addClass('active');
		}, 300);
		setTimeout(function () {
			$(ob).removeClass('active ' + data.class);
			$(ob).find('.' + sbClass + '-text').text('');
			$(ob).find('.' + sbClass + '-action').text('');

			var sbEvent = $.Event('snackbar.closed');
			$(ob).trigger(sbEvent, [$(ob)]);
		}, data.timeout);
		$('.' + sbClass + '-action').on('click', function () {
			$(this).parent('.' + sbClass).removeClass('active ' + data.class);
			$(this).parent('.' + sbClass).find('.' + sbClass + '-text').text('');
			$(this).parent('.' + sbClass).find('.' + sbClass + '-action').text('');
			setTimeout(data.actionHandler, 0);
		});
		return this;
	};

	/**!
	Dialog Box Plugin
	* @version 1.0.3
	*/
	$.fn.dialog = function (data) {
		var dClass = "dialog";
		this.each(function (i, ob) {
			var ds = $(ob).data();

			if (data.action === 'init') {
				data.backdrop = (data.backdrop === undefined) ? false : getBoolean(data.backdrop);
				ds.backdrop = (ds.backdrop === undefined) ? data.backdrop : getBoolean(ds.backdrop);
			} else {
				ds.backdrop = (ds.backdrop === undefined) ? false : getBoolean(ds.backdrop);
				data.backdrop = (data.backdrop === undefined) ? ds.backdrop : getBoolean(data.backdrop);
				ds.backdrop = data.backdrop;
			}

			if (ds.buttons !== undefined && typeof (ds.buttons) === "string") {
				ds.buttons = JSON.parse(ds.buttons.replace(/'/g, '"'))
			} else {
				ds.buttons = undefined;
			}
			data = {
				action: ds.action || data.action || 'init',
				class: ds.class || data.action || 'dialog',
				width: ds.width || data.width || 'auto',
				height: ds.height || data.height || 'auto',
				message: ds.message || data.message || '',
				backdrop: ds.backdrop,
				buttons: ds.buttons || data.buttons || [{
					class: 'R',
					label: 'ok',
					action: void (0)
				}]
			}

			if (data.action === 'init') {
				dialog.init(ob, data);
			}
			if (data.action === 'open') {
				dialog.open(ob, data);
			}
			if (data.action === 'close') {
				dialog.close(ob, data);
			}
		});
	}

	/* popup Plugin */
	$.fn.popup = function (data) {
		var vArgs = Array.prototype.slice.call(arguments, 1);
		this.each(function (i, ob) {
			if (data.action === "init") {
				popup.init(50, 50, 0, 0);
			}
			if (data.action === "refresh") {
				popup.refresh();
			}
			if (data.action === "open") {
				popup.open(ob, data.width, data.height);
			}
			if (data.action === "close") {
				popup.close(ob);
			}
			if (data.action === "loader") {
				popup.loader(ob, data.loading);
			}
		});
	};

	/* Fast Inputs with ENTER key stroke */
	/**
	 * Version 1.1
	 */
	$.fn.fastInput = function (action) {
		var formControls = 'input,button,select,textarea',
			accessibles = formControls + ',a',
			findAccessibleElement = function (ob) {
				var accCtrl = $(accessibles)[$(accessibles).index(ob) + 1];
				if (
					$(accCtrl).is(':hidden') ||
					$(accCtrl).is(':disabled') ||
					$(accCtrl).is('[readonly]') ||
					$(accCtrl).is('[tabindex="-1"]') ||
					$(accCtrl).is('[data-ignore-focus="true"]') ||
					typeof ($(accCtrl)) === undefined
				) {
					// console.log($(accCtrl));
					return findAccessibleElement($(accCtrl));
				} else {
					// console.log($(accCtrl));
					return $(accCtrl);
				}
			},
			searchFormCtrl = function (ob) {
				var accCtrl = $(accessibles)[$(accessibles).index(ob) + 1],
					frmCtrl = $(formControls)[$(formControls).index(ob) + 1];
				if (
					$(frmCtrl).is(':hidden') ||
					$(frmCtrl).is(':disabled') ||
					$(frmCtrl).is('[readonly]') ||
					$(accCtrl).is('[tabindex="-1"]') ||
					$(accCtrl).is('[data-ignore-focus="true"]') ||
					typeof ($(frmCtrl)) === undefined
				) {
					// console.log($(frmCtrl));
					return searchFormCtrl($(frmCtrl));
				} else {
					// console.log($(frmCtrl));
					return $(frmCtrl);
				}
			},
			focusFormCtrl = function (ob, currOb) {
				if (
					$(ob).length <= 0 ||
					$(ob).is(':hidden') ||
					$(ob).is(':disabled') ||
					$(ob).is('[readonly]') ||
					$(ob).is('[tabindex="-1"]') ||
					$(ob).is('[data-ignore-focus="true"]') ||
					typeof ($(ob)) === undefined
				) {
					return searchFormCtrl($(currOb));
				} else if ($(ob).length > 0) {
					return $(ob);
				} else {
					return searchFormCtrl($(currOb));
				}
			};

		window.findAccessibleElement = findAccessibleElement;
		window.searchFormCtrl = searchFormCtrl;
		window.focusFormCtrl = focusFormCtrl;

		if (action === "init") {
			$(this).off('keydown.fastInput').on('keydown.fastInput', formControls, function (e) {
				switch (e.which) {
					case kb.ENTER:
						if ($(this).is('[class*="submit_"]')) {
							var param = 'submit_' + $(this).attr('class').split('submit_')[1];
							param = param.split(' ')[0];
							// console.log(param);
							return submitForm(param.split('_'));
							/*
							--SYNTAX--
							submitForm(params[mode, action, customArg1, customArg2, ...])
							*/
						}
						var tgt = (
							$.escapeSelector($(this).attr('data-focus')) !== undefined
							|| $(this).is('[data-focus]') !== false) ? '#' + $.escapeSelector($(this).attr('data-focus')) : '',
							fiEvent = $.Event('fastInputTargeting'),
							objTgt = this;
						tgt = (
							$.escapeSelector($(this).attr('data-focus')) !== undefined
							|| $(this).is('[data-focus]') !== false
						) ? '#' + $.escapeSelector($(this).attr('data-focus')) : ''
						$(document).trigger(fiEvent, [tgt, this]);

						if (fiEvent.isDefaultPrevented() === true) { return true; }
						// console.log(tgt);
						if (tgt !== '') {
							// console.log('IF > ' + tgt);
							objTgt = focusFormCtrl(tgt, this);
							// console.log(objTgt);
							$(objTgt).focus();//.select();
						} else {
							// console.log('ELSE > ' + tgt);
							objTgt = searchFormCtrl(this);
							// console.log(objTgt);
							$(objTgt).focus();//.select();
						}
						break;
				}
			});
		}
	};

	/*Disable Form Controls*/
	$.fn.preventAccess = function (settings) {
		settings = {
			formControl: settings.formControl || '.form-control',
			groupPrevent: settings.groupPrevent || false,
			group: sanitizeSelector(settings.group || '[data-disabled-group]')
		};

		function sanitizeSelector(selector) {
			// Remove any potentially harmful characters or patterns
			return selector.replace(/[^a-zA-Z0-9_\-#\.\[\]=:]/g, '');
		}

		this.each(function (i, el) {
			if (settings.groupPrevent === true) {
				$.find(settings.group).each(function (g, gel) {
					var fc = $(gel).find(settings.formControl);
					$(fc).each(function (f, fel) {
						$(fel).attr({
							'tabindex': '-1',
							'disabled': 'disabled'
						});
						$(fel).off('focus.access').on('focus.access', function () {
							$(fel).blur();
						});
					});
				});
			} else {
				$(el).each(function (f, fel) {
					$(fel).attr({
						'tabindex': '-1',
						'disabled': 'disabled'
					});
					$(fel).off('focus.access').on('focus.access', function () {
						$(fel).blur();
					});
				});
			}
		});
		return this;
	};

	function sanitizeText(text) {
		// Allow only alphanumeric characters, hyphens, underscores, and periods
		var regex = /^[a-zA-Z0-9-_\.]+$/;
		if (regex.test(text)) {
			return text;
		} else {
			console.warn('Invalid outputTo value:', text);
			return '';
		}
	}
	/* Function to validate CSS selector */
	function isValidSelector(selector) {
		// Regular expression to validate CSS selector
		var regex = /^[a-zA-Z0-9\-_#\.\s]+$/;
		return regex.test(selector);
	}

	/*Multi selector ComboBox*/
	$.fn.multiselector = function (settings) {
		this.select = function (num) {
			console.log('newFn', num);
		}
		s = {
			class: settings.class || '',
			placeholder: settings.placeholder || 'Choose',
			csvDispCount: settings.max || 3,
			captionFormat: settings.selectMsg || '{0} Selected',
			captionFormatAllSelected: settings.selectMsgAll || '{0} all selected!',
			floatWidth: settings.screen || 500,
			forceCustomRendering: (settings.forceRender === undefined) ? false : getBoolean(settings.forceRender),
			nativeOnDevice: settings.devices || ['Android', 'BlackBerry', 'iPhone', 'iPad', 'iPod', 'Opera Mini', 'IEMobile', 'Silk'],
			outputAsCSV: (settings.csv === undefined) ? false : getBoolean(settings.csv),
			csvSepChar: settings.csvSeperator || ',',
			okCancelInMulti: (settings.buttons === undefined) ? false : getBoolean(settings.buttons),
			triggerChangeCombined: (settings.live === undefined) ? true : getBoolean(settings.live),
			selectAll: (settings.all === undefined) ? false : getBoolean(settings.all),
			search: (settings.search === undefined) ? false : getBoolean(settings.search),
			searchText: settings.searchText || 'Search...',
			noMatch: settings.error || 'No matches for "{0}"',
			prefix: settings.prefix || '',
			locale: settings.locale || ['OK', 'Cancel', 'Select All'],
			top: (settings.up === undefined) ? false : getBoolean(ds.top),
			tooltip: (settings.showTitle === undefined) ? true : getBoolean(ds.tooltip),
			outputTo: sanitizeText(settings.outputTo) || '',
			outputTemplate: settings.outputTemplate || "<b> &gt; {{$}} </b>",
			outputSrcAttr: settings.outputSrcAttr || "value"
		}
		return this.each(function (idx, el) {
			var ds = $(el).data();
			settings = {
				class: ds.class || s.class,
				placeholder: ds.placeholder || s.placeholder,
				csvDispCount: ds.max || s.max,
				captionFormat: ds.selectMsg || s.selectMsg,
				captionFormatAllSelected: ds.selectMsgAll || s.selectMsgAll,
				floatWidth: ds.screen || s.screen,
				forceCustomRendering: (ds.forceRender === undefined) ? s.forceRender : getBoolean(ds.forceRender),
				nativeOnDevice: ds.devices || s.devices,
				outputAsCSV: (ds.csv === undefined) ? s.csv : getBoolean(ds.csv),
				csvSepChar: ds.csvSeperator || s.csvSeperator,
				okCancelInMulti: (ds.buttons === undefined) ? s.buttons : getBoolean(ds.buttons),
				triggerChangeCombined: (ds.live === undefined) ? s.live : getBoolean(ds.live),
				selectAll: (ds.all === undefined) ? s.all : getBoolean(ds.all),
				search: (ds.search === undefined) ? s.search : getBoolean(ds.search),
				searchText: ds.searchText || s.searchText,
				noMatch: ds.error || s.error,
				prefix: ds.prefix || s.prefix,
				locale: ds.locale || s.locale,
				up: (ds.top === undefined) ? s.top : getBoolean(ds.top),
				showTitle: (ds.tooltip === undefined) ? s.tooltip : getBoolean(ds.tooltip),
				outputTo: ds.outputTo || s.outputTo,
				outputTemplate: ds.outputTemplate || s.outputTemplate,
				outputSrcAttr: ds.outputSrcAttr || s.outputSrcAttr
			}
			// console.log(settings);
			$(el).SumoSelect(settings);

			$(el).closest('.SumoSelect').addClass(settings.class);

			if ($.trim(settings.outputTo) !== '' && isValidSelector(settings.outputTo)) {
				var output = $.find(settings.outputTo),
					outputTemplate = settings.outputTemplate.replace(/'/g, '"'),
					outputSrc = settings.outputSrcAttr,
					act = null;

				outputTemplate = outputTemplate.replace(/{{\$action\=([a-z-]+)\(\)}}/, '').split('{{$}}');
				act = settings.outputTemplate.match(/{{\$action\=([a-z-]+)\(\)}}/);

				if (act !== null) {
					act = act[0].split('{{')[1].split('}}')[0].split('=')[1];
				}

				$(el).attr('data-action', act);

				if ($.find(output).length <= 0) { return; }

				// $(el).attr('data-output-to', settings.outputTo);
				// console.log(el)
				$(el).off('change.multiselector').on('change.multiselector', function () {
					// console.log( $(this).attr('data-output-to') )
					var av = new Array,
						sv = new Array;
					// console.log(outputSrc)
					$(this).find('option:selected').each(function () {
						var ov = $(this).attr(outputSrc),
							v = $(this).attr('value');
						sv.push(v);
						av.push(ov);
						sv = sv.filter(onlyUnique);
						av = av.filter(onlyUnique);
					});

					$.find(output).html('');
					for (k in av) {
						// console.log(outputTemplate, sv[k]);
						var d = (outputTemplate[0].replace(/>/g, ' data-val="' + escapeHtml(sv[k]) + '">')) + av[k] + escapeHtml(av[k]) + outputTemplate[1];

						$(output).attr('data-rel', '#' + $(el).attr('id'));
						$(output).append(escapeHtml(d));
						$(output).find('> *').off('click.multiselector').on('click.multiselector', function () {
							$($(output).attr('data-rel'))[0].sumo.unSelectItem($(this).attr('data-val'));
							if ($.trim(act) !== '') {
								if (typeof act === "function") {
									act.apply();
								} else if (typeof act === "string") {
									var fnName = act.split('('),
										fnparams = fnName[1].split(')')[0].split(','),
										fn = window[fnName[0]];
									if (typeof fn === "function") { fn.apply(null, fnparams) };
								}
							}
						});
					}
				});
			}
		});
	}

	/*Draggable Areas, Windows */
	$.fn.dragger = function () {
		return this.each(function (idx, el) {
			$(el).draggable({
				handle: ".titlebar" || ".popHeader",
				containment: $(el).attr('data-draggable'),
				scroll: false
			});
		});
	};
	/*HistoryBoard*/
	$.fn.historyBoard = function (action) {
		this.each(function (idx, el) {
			if (action === 'init') {
				var bw = $(el).data('width'),
					bh = $(el).data('height'),
					t = $(el).data('top'),
					r = $(el).data('right'),
					b = $(el).data('bottom'),
					l = $(el).data('left');
				$(el).css({
					'width': bw || '50%',
					'height': bh || '200px',
					'top': t || '120px',
					'right': r || '15px',
					'bottom': b || 'auto',
					'left': l || 'auto'
				});
			}

			$(document).off('click.historyBoard').on('click.historyBoard', '[data-history]', function () {
				var tgt = $(this).is('[data-history]');
				console.log(tgt)
				if (tgt) {
					tgt = '#' + $(this).data('history') + 'History'
					$('.historyBoard').hide();
					$(tgt).show();
				} else {
					$('.historyBoard').hide();
				}
			});

			$(el).off('click.historyBoard').on('click.historyBoard', '.close', function () {
				$(el).hide();
			});
		});
	};

	/**!
	 * JsonPro
	 * @version 1.0
	*/
	$.fn.jsonPro = function (action /*, ...*/) {
		var vArgs = Array.prototype.slice.call(arguments, 1),
			settings = vArgs[0],
			source = settings.source,
			json = '';
		settings = {
			'source': settings.source || '[data-jp-params]',
			'jsonObject': settings.jsonObject || false,
			'filter': settings.filter || 'all'
		};
		var jp = {
			jsonBuilder: function (row) {
				var args = Array.prototype.slice.call(arguments, 1),
					json = ($(row).length > 1) ? '[' : '';
				$(row).each(function (si, sel) {
					json += '{';
					json += jp.propertyList($(sel).find(source), source);
					//json = json.slice(0, -1) + '},';
				});
				//json += "\"" + el.split('_')[0] + "\"" + ':' + el.split('_')[1]  + ',';
				json = json.slice(0, -1);
				json = ($(row).length > 1) ? json + ']' : json;
				return json;
			},
			propertyList: function (dataEl, src) {
				var pList = '',
					jpAttr = src.replace(/[\[\]]/g, ''), jpParams, config;
				$(dataEl).each(function (si, sel) {
					var jpParams = $(sel).attr(jpAttr).split(/[\{\}]/g);
					config = {
						'property': jpParams[0].split(':')[0],
						'datatype': (jpParams[0].split(':')[1]) ? jpParams[0].split(':')[1].split('{')[0] : 'String',
						'array': jpParams[1]
					};

					if (!isNull($(sel).val())) {
						if (config.datatype === "Array") {
							var getValFrom = config.array.match(/\<([^<>])*\>/g);
							for (var i = 0; i < getValFrom.length; i++) {
								getValFrom[i] = getValFrom[i].replace(/[\<\>]/g, '');
							}
							for (var i = 0; i < getValFrom.length; i++) {
								var pv = $(sel).closest('[data-current]').find('.' + getValFrom[i]).val();
								config.array = config.array.replace('<' + getValFrom[i] + '>', pv);
							}
							pList += '"' + config.property + '":[';
							// $(getValFrom).each(function(gi, gel) {

							// });
							pList += jp.arrayToJson((config.array).split(','));
							pList = pList.slice(0, -1);
							pList += '],';
							// pList += '\"' + config.property + '\":' + config.array  + ',';
						} else if (config.datatype === "Number") {
							pList += '"' + config.property + '":' + $(sel).val() + ',';
						} else {
							pList += '"' + config.property + '":"' + $(sel).val() + '",';
						}
					} else {

					}
				});
				pList = pList.slice(0, -1) + '},'
				//pList = pList.slice(0, -1)
				return pList;
			},
			arrayToJson: function (arr) {
				var json = '{', config;
				$.each(arr, function (i, el) {
					config = {
						'property': el.split(':')[0],
						'datatype': (el.split(':')[1]) ? el.split(':')[1].split('{')[0] : 'String',
						'value': el.split(':')[2]
					};
					if (!isNull(config.value)) {
						if (config.datatype === "Array") { }
						else if (config.datatype === "Number") {
							json += '"' + config.property + '":' + config.value + ',';
						} else {
							json += '"' + config.property + '":"' + config.value + '",';
						}
					}
				});

				json = json.slice(0, -1);
				json += '},';
				return json;
			}
		};

		this.each(function (i, ob) {
			// console.log(vArgs, ob, source);
			var row;
			json = '';
			if (action === "init") {
				$(source).each(function (si, el) {
					$(el).closest('[data-jp-row]').attr('data-jp-row', false);
					$(el).off('change.jsonPro').on('change.jsonPro', function () {
						$(el).closest('[data-jp-row]').attr('data-jp-row', true);
					});
				});
			} else if (action === "build") {
				if (settings.filter === 'all' ||
					settings.filter === 'default' ||
					isNull(settings.filter)) {
					row = $(ob).find('[data-jp-row]');
					json = jp.jsonBuilder(row);
				} else if (settings.filter === 'edit') {
					row = $(ob).find('[data-jp-row="true"]');
					json = jp.jsonBuilder(row);
				}
			} else if (action === "") {
			}
			// console.log(json);
			// console.log($.parseJSON(json));

			if (!json) {
				// console.log(json)
				json = {} || this;
			} else {
				if (settings.jsonObject === true) {
					json = $.parseJSON(json);
				}
			}
			// return json;
		});

		return json;
	};
}(jQuery));

/*File Upload*/
$(function () {
	"use strict";
	// $('a[href*="#"]:not([href="#"]):not([data-toggle])').click(function () {
	// 	if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '') && location.hostname == this.hostname) {
	// 		var target = $(this.hash);
	// 		target = target.length ? target : $('[name=' + this.hash.slice(1) + ']');
	// 		if (target.length) {
	// 			$('html, body').animate({
	// 				scrollTop: (target.offset().top - 130)
	// 			}, 1000);
	// 			return false;
	// 		}
	// 	}
	// });

	/* Links */
	$("[aria-link]").each(function (a) {
		$(a).attr('tabindex', 0);
	});
	$("[aria-link]").on('click', function (e) {
		var url = $(this).attr('aria-link'),
			tag = '' + e.target.tagName.toLowerCase();
		// console.log(tag);
		if (tag === 'button' || tag === 'a') {
			return;
		} else if ($(this).is('[data-target="_blank"]')) {
			window.open(url, "_blank");
		} else {
			window.location.href = url;
			window.location.reload;
		}
	});

	/*Disable Form Controls*/
	// $('[data-disabled="true"]').each(function(i, el) {$(el).attr('tabindex','-1');});
	var formControls = 'input,button,select,textarea';
	$(document).on('focus', '[data-disabled="true"]', function () {
		//$(this).blur();
		$($(formControls)[$(formControls).index(this) + 1]).focus();//.select();//.keydown();
	});

	/*File Upload*/
	$('.fileUpload input:file').change(function () {
		var fId = $(this).attr('id'),
			url = $('#' + fId).val() || $(this).attr('placeholder');
		$(this).parent('.fileUpload').find("span:first-child").text(url);
	});
	$('.fileUpload').each(function (i, el) {
		var fId = $(el).find('input:file').attr('id'),
			url = $('#' + fId).val() || $(this).find('input:file').attr('placeholder');
		$(el).find("span:first-child").text(url);
	});

	$('input[type="number"]').on('keyup focus', function () {
		var min = $(this).attr('min') || 0;
		this.maxLength = (this.maxLength <= 0) ? 255 : this.maxLength;
		if (this.value.length >= (this.maxLength)) {
			this.value = this.value.slice(0, this.maxLength);
		}
		// this.value = this.value.replace(/^0+/, min);
	});

	/*Tabbed Wizard*/
	$('[data-type="wizard"] .step').click(function () {
		var op = $.trim($(this).data('nav'));
		op = op.toLowerCase();
		var id = $(this).parents('.tab-pane').attr('id');
		switch (op) {
			case 'next':
				id = $(this).parents('.tab-pane').next().attr('id');
				$('[href=#' + id + ']').parents('li').attr('aria-disabled', 'false');
				break;
			case 'prev':
				id = $(this).parents('.tab-pane').prev().attr('id');
				$('[href=#' + id + ']').parents('li').attr('aria-disabled', 'false');
				break;
			case 'first':
				id = $('[aria-controls="' + id + '"]').closest('.nav-tabs').find('[aria-disabled="false"]:first a').attr('href').split('#')[1];
				break;
			case 'last':
				id = $('[aria-controls="' + id + '"]').closest('.nav-tabs').find('[aria-disabled="false"]:last a').attr('href').split('#')[1];
				break;
			case 'all':
				$('[href=#' + id + ']').closest('.nav-tabs').find('li').attr('aria-disabled', 'false');
				break;
			default: break;
		}
		$('[href=#' + id + ']').tab('show');
	});

	$('.btn-group.optional .btn').click(function () {
		var id = $(this).closest('.btn-group').find('.btn.active').attr('id');
		$(this).closest('.btn-group').find('.btn').removeClass('active');
		$(this).addClass('active');
		if (id !== undefined) {
			$(this).attr('data-prev', id);
		} else {
			$(this).attr('data-prev', $(this).attr('id'));
		}
	});

	/*Table Sorter*/
	$('[aria-table-sort="true"]').each(function (i, el) {
		$(el).tablesorter({
			textExtraction: function (node) {
				// iterates all childs elements inside td and return data from the last child
				if (node === null) { return null; }
				node = $(node);
				while (node.children().length > 0) { node = node.children(":first"); }
				if (node[0].tagName.toUpperCase() === "INPUT") {
					if (node.attr("type").toUpperCase() === "CHECKBOX") { return node.attr("checked").toString(); }
					else { return $.trim(node.val()); }
				}
				else { return $.trim(node.text()); }
			}
		});
	});

	// $(document).on('focus', '.form-control, input, textarea', function (e) {
	// 	$(this).select();
	// });
	/*Dropdown Custom ComboBox*/
	$('.dropdown.combobox').each(function (i, el) {
		$(el)
			.attr('data-id', $(el).find('ul > li.active').attr('data-value'))
			.find('.selected').text($(el).find('ul > li.active:first > a > span').text());
	});
	$('.dropdown.combobox > ul > li > a').click(function () {
		var pOb = $(this).parents('.dropdown.combobox')[0];
		$(pOb).find('.active').removeClass('active');
		$(this).parent('li').addClass('active');

		$(pOb)
			.attr('data-id', $(pOb).find('ul > li.active').attr('data-value'))
			.find('.selected').text($(pOb).find('ul > li.active:first > a > span').text());
	});

	/*Create Table Columns*/
	$('[data-create-cols]').click(function () {
		var tgt = '#' + $(this).data('createCols'),
			c = $(tgt + ' tbody tr:first td').length;
		/*$(tgt+' tbody tr:first').append("<td><a href=''>Delete</a> Col "+(c+1)+"</td>");
		$(tgt+' tbody tr:gt(0)').append("<td>Col</td>");*/
		createColumn(tgt, c - 2);

		$(tgt + ' tbody tr td input[type="number"]:last').blur();
	});
});

function createElementWithText(element, text) {
	var el = document.createElement(element);
	el.innerText = text;
	return el;
}

function getMatLabel(attr, lbl, op) {
	const labelEl = document.createElement('label');
	labelEl.setAttribute('for', attr["id"].value);
	labelEl.classList.add('mtl-label');
	labelEl.innerHTML = lbl;
	labelEl.innerHTML += '<small>' + op.message + '</small>';
	return labelEl;
}

/*!
 * Author: Infospica, http://www.infospica.com/
 * Copyright @ 2015 Infospica Consultancy Services
 */

/*global $, jQuery, alert, console*/

function fillTable(table, data) {
	"use strict";
	/*
	 *	EXAMPLE
	 *	-------
	 *	fillTable('#shipmentPlan',jsonVar,'<input type="text" class="form-control" value="','">');
	 */
	var vArgs = Array.prototype.slice.call(arguments, 2),
		i = 0,
		t = 0,
		row = $("<tr />"),
		key;
	for (i = 0; i < data.length; i += 1) {
		row = $("<tr />");
		$(table).append(row);

		if (vArgs.length <= 0 || vArgs === null) {
			for (key in data[i]) {
				if (key) {
					row.append($("<td>" + data[i][key] + "</td>"));
				}
			}
		} else {
			for (t = 0; t < vArgs.length; t += 2) {
				for (key in data[i]) {
					if (key) {
						row.append($("<td>" + vArgs[t] + data[i][key] + vArgs[t + 1] + "</td>"));
					}
				}
			}
		}
	}
}

function clearTable(table) {
	"use strict";
	$(table).find('tbody tr').each(function (i, el) {
		$(el).remove();
	});
}

$(function () {
	"use strict";
	$('[data-toggle="tooltip"]').tooltip();
	$('[data-control="dialog"]').dialog({
		action: 'init'
	});
	page.refresh();
});

$(window).resize(function () {
	"use strict";
	page.getRatio();
	page.refresh();
	// if($('.header .navbar-brand').hasClass('minimise') && !$('[data-name="mainSideBar"]').hasClass('minimize')){
	// 	$('.header .navbar-brand').removeClass('minimise');
	// }
	// if(!$('.header .navbar-brand').hasClass('minimise') && $('[data-name="mainSideBar"]').hasClass('minimize')){
	// 	$('.header .navbar-brand').addClass('minimise');
	// }
});

$(window).scroll(function () {
	"use strict";
});

var w, h;
var /*desktop=1200,*/tablet = 767/*,mobile=320*/;

var page = {
	refresh: function () {
		"use strict";
		//$(window).trigger('resize');
		duplicator.init();
		$('.popup').popup('refresh');

		$('[data-control="material"]').materialise({
			floatingLabel: true,
			placeholderAsLabel: true
		});
		// $('.popup').popup("refresh");
		$('[data-fast-input="true"]').fastInput('init');
		$('.dataTable').fastInput('init');

		$('[data-fastinput="true"]').fastInput('init');

		$('[data-disabled="true"]').preventAccess({});
		$('[data-disabled-group="true"]').preventAccess({
			formControl: '.form-control',
			groupPrevent: true,
			group: '[data-disabled-group]'
		});

		$('[data-draggable]').dragger();
		$('.historyBoard').historyBoard();

		page.reInitLayout();

		$('.dataTable').find('input,textarea,a,button,select').off('focus.dt.global').on('focus.dt.global', function () {
			$('.dataTable tr').removeClass('active');
			$(this).closest('tr').addClass('highlight active');
		});
	},
	getRatio: function () {
		"use strict";
		w = $(window).width();
		h = $(window).height();
	},
	reInitLayout: function () {
		"use strict";
		page.getRatio();
		var hh = $('.header .navbar').outerHeight(),
			fh = $('footer.footer').outerHeight();
		var hgt = h - (hh + fh + 20);

		$('.sidebar > .fwhFixer').css('min-height', hgt);
		$('.layout').css('height', h - fh);

		$('.page').css('min-height', h - fh);

		$('.pageContent').css('min-height', (hgt - (hh - 20)));

		var wa = $('.conArea').find('.workArea');
		$(wa).each(function (i, el) {
			if ($(wa).is('[data-scroll]') || $(wa).is('[data-scroll-x]') || $(wa).is('[data-scroll-y]')) {
				$(el).parents('.page').css({
					'height': (h - fh),
					'overflow': "hidden"
				});

				/*$(el).parents('body').find('.sidebar > .fwhFixer').css('height','auto');
				 $(el).parents('body').find('.sidebar').css('height','auto');*/
				var p = $(el).offset().top;
				//console.log('pos = '+p)
				//console.log('wh > h = ' + h + ' > ' + hgt)
				$(el).parent('.conArea').css({
					'height': (hgt) - $(el).parent('.conArea').offset().top
				});
				p = p + (parseInt($(el).parent('.conArea').css('padding-bottom')));
				//console.log('pos++ = '+p)
				$(el).css('height', h - (p + fh));
			}
		});
	}
};

$('.menuToggler[data-toggle="mainSideBar"]').click(function () {
	"use strict";
	page.getRatio();
	if ($('.header .navbar-brand').hasClass('minimise')/* && !$('[data-name="mainSideBar"]').hasClass('minimise')*/) {
		$('.header .navbar-brand').removeClass('minimise');
	} else if (w >= tablet) {
		$('.header .navbar-brand').addClass('minimise');
	}
});

//open popup

function openPopup(url, qs, focusId) {
	url = location.origin + url;
	openPopupId('frameId', url, qs, focusId);
}

//Deprecated remove and use above
function openPopupId(id, url, qs, focusId) {
	if (url !== null && url !== '') {
		if (qs === undefined || qs === null)
			qs = '';
		popup.open('#' + id, 99, 99, {
			units: '%',
			xsource: url + qs,
			setText: '',
			setDataFocus: focusId
		});
	}
}

function showError(mess) {
	setTimeout(function () { showalert(mess, '#messages', 'danger', -1); mess = ''; }, 300);
}
function showSuccess(mess) {
	setTimeout(function () { showalert(mess, '#messages', 'danger', -1); mess = ''; }, 300);
}
// custom autocomplete wrapper

/**
 * <pre>
 * Usage
 *
 * var pCols = [
 * {name: 'Product', width: '220px', valueField: 'pdrname'},
 * {name: 'Packing', width: '80px', valueField: 'unit'},
 * {name: 'Batch', width: '80px', valueField: 'batch'},
 * {name: 'Expiry', width: '80px', valueField: 'exp'},
 * {name: 'Price', width: '60px', valueField: 'price'}
 * ];
 *
 * $(document).on('dataTableNavigating', '.mcAutoCombo', function (e, ob) {
 * if($(ob).val() === ''){
 *  e.preventDefault();
 * }
 * appAutoComplete(".product", "/scm/purchaseReqItem", pCols);
 * });
 * </pre>
 * @param {type} ob
 * @param {type} selector
 * @param {type} urlName
 * @param {type} colSchema
 * @param {type} params
 * @returns {undefined}
 */
function appAutoComplete(ob, selector, urlName, colSchema, params) {
	var returnJson;
	$.ajax({
		url: urlName,
		type: "post",
		async: false,
		data: params,
		success: function (data) {
			returnJson = jQuery.parseJSON(data);
		}
	});

	autoCombo.multiColumn('.mcAutoCombo',
		{
			'selector': selector,
			'columns': colSchema,
			'src': returnJson
		});
}


// show hide widget/dialog

function hide(name) {
	$('#' + name).hide();
}

function wdgOff(obj) {
	PF(obj).hide();
}
function wdgOn(obj) {
	PF(obj).show();
}

function toTop() {
	$("html, body").animate({ scrollTop: 0 }, '600');
}

function block() {
	PF('wdgBlock').show();
	// $('body').addClass('onLoading');
}
function unblock() {
	//  console.log(blocker + ' unblock');
	PF('wdgBlock').hide();
	// $('body').removeClass('onLoading');
}
function unblockTop() {
	unblock();
	toTop();
}
function appInit() {

	if (typeof jsf !== 'undefined') {
		jsf.ajax.addOnEvent(function (data) {
			var ajaxstatus = data.status; // Can be "begin", "complete" and "success"

			switch (ajaxstatus) {
				case "begin": // This is called right before ajax request is been sent.
					if (data.source.type == "submit") {
						data.source.disabled = true;
					}
					//    blocker = 'ajax begin block'
					//    console.log(blocker);
					block();
					break;
				case "complete": // This is called right after ajax response is received.
					if (data.source.type == "submit") {
						data.source.disabled = false;
					}
					unblock();
					break;
				case "success": // This is called when ajax response is successfully processed.
					// NOOP.
					break;
			}
		});
	}
	$('a').click(function () {
		if (!($(this).is('.ignoreLoad'))) {
			//  blocker = 'a block'
			//   console.log(blocker);
			block();
		} else {

		}
	});
	$(document).ready(function () {
		unblock();
	});
}
//var blocker = "";
//search highlighter
function searchHighlight(field) {
	highLight($("input[name='" + field + "']"), ".ui-datatable-data tr td");//td:nth-child(3)
}
function menuHighlight(field) {
	highLight($("input[name='" + field + "']"), ".menu div ul li a");//td:nth-child(3)
}
//hightlight field
function highLight(field, ele) {
	var keyword = field.val(),
		options = {
			"element": "span",
			"className": "dataHighlight",
			"separateWordSearch": true
		},
		$ctx = $(ele);
	$ctx.unmark({
		done: function () {
			$ctx.mark(keyword, options);
		}
	});
}

function preListSelect(widget, wdgOk, wdgConfirm) {
	var rowcount = PF(widget).getSelectedRowsCount();
	if (rowcount === 0) {
		PF(wdgOk).show();
		return false;
	} else {
		if (wdgConfirm !== null)
			return PF(wdgConfirm).show();
		else
			return true;
	}
}

function callDtFilter(val, table) {
	var k = "#" + table + "Table\\:globalFilter";
	$(k).val(val);
	var k2 = table + "Widget";
	PF(k2).filter();
	return true;
}



//Inline edit function

/**
 * editRow('proTab', 2);
 * @param {type} rowEditId - proTab
 * @param {type} focusRow - col no to focus
 * @param {type} formName - Name of form not implemented
 * @returns {undefined}
 */
function editRow(rowEditId, focusRow, formName) {
	if ($('#' + rowEditId + '\\:0\\:editRow').length > 0) {
		$('#' + rowEditId + '\\:0\\:editRow > .ui-row-editor-pencil').trigger('click');
		$('#f1 :input[type="text"]')[focusRow].focus();
	}
}
/**
 * saveRow('proTab\\:0');
 * @param {type} rowEditId
 * @returns {undefined}
 */
function saveRow(rowEditId) {
	$('#' + rowEditId + '\\:editRow > .ui-row-editor-check').trigger('click');
}

/**
 * addRow('addBtn');
 * @param {type} addBtnId
 * @returns {undefined}
 */
function addRow(addBtnId) {
	$('#' + addBtnId).trigger('click');
}


/**
 *
 * To submit the form on press of enter key add a simple style class
 * 1 argument sholuld always be "submit"
 * 2 argument is an identifier to identify the submit, helps to control the submit based on this argument.
 * 3 argument is the row number of the datatable
 *
 * Text field  <input type='text' class="submit_prod_5" />
 * "submit_prod_5"
 *
 * prod - is the identifier
 * 5 - is the row number
 *
 * function submitForm(params) {
 if (params[1] == 'mytext1') {
 saveRow('proTab\\:' + params[2]);
 }
 return false;
 }
 
 function saveHotkey() {
 saveRow('proTab\\:0');
 }
 
 */

