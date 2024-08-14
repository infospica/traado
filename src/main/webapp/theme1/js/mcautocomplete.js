/*
 * jQuery UI Multicolumn Autocomplete Widget Plugin 2.2
 *
 * Depends:
 *   - jQuery UI Autocomplete widget
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 */
$.widget('custom.mcautocomplete', $.ui.autocomplete, {
  _create: function () {
    "use strict";
    this._super();
    this.widget().menu("option", "items", "> :not(.ui-widget-header):not(.link)");
  },
  _renderMenu: function (ul, items) {
    "use strict";
    var self = this, thead;

    if (this.options.showHeader) {
      var table = $('<div class="ui-widget-header" style="width:100%"></div>');
      // Column headers
      $.each(this.options.columns, function (index, item) {
        table.append('<span style="float:left;width:' + item.width + ';">' + item.name + '</span>');
      });
      table.append('<div style="clear: both;"></div>');
      ul.append(table);
    }

    /*ul.css('width',ul.innerWidth() + 300 +'px !important');*/

    // List items
    $.each(items, function (index, item) {
      self._renderItem(ul, item);
    });
  },
  _renderItem: function (ul, item) {
    "use strict";
    var t = '',
            result = '';

    $.each(this.options.columns, function (index, column) {
      t += '<span style="float:left;width:' + column.width + ';">' + item[column.valueField ? column.valueField : index] + '</span>';
    });

    result = $('<li></li>')
            .data('ui-autocomplete-item', item)
            .append('<a class="mcacAnchor">' + t + '<div style="clear: both;"></div></a>')
            .appendTo(ul);

    return result;
  }/*,
   _resizeMenu: function(ul,item){

   }*/
});

