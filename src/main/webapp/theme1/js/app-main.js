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

function showError(mess){
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
  $("html, body").animate({scrollTop: 0}, '600');
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
  $("input[name='" + field + "']").on("keyup", function () {
    highLight($("input[name='" + field + "']"));
  });
}

//hightlight field
function highLight(field) {
  var keyword = field.val(),
          options = {
            "element": "span",
            "className": "dataHighlight",
            "separateWordSearch": true
          },
          $ctx = $("div.ui-datatable-scrollable-body");
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
