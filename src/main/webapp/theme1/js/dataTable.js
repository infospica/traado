/*global $, dataTable, historyView, pCols, product, pPurchaseCols */

var dataTable = {
  init: function () {
    "use strict";
    $('.dataTable').each(function (i, el) {
      $(el).find('tbody tr').each(function (ri, re) {
        $(re).attr('aria-count', ri + 1);
      });
      $(el).find('td').attr('aria-enabled', 'true');
      $(el).find('[disabled]').parents('td').attr('aria-enabled', 'false');
      $(el).find('[readonly]').parents('td').attr('aria-enabled', 'false');
      $(el).find('[data-disabled]').parents('td').attr('aria-enabled', 'false');
      $(el).find('.form-control').attr('aria-open', false);
    });

    $('.dataTable').on('focus', '.form-control', function () {
      // if ($(this).val() === '') {
      // 	$(this).keydown();
      // }
    });

    $(document).on('focus', '[data-open="true"]', function () {
      if ($(this).val() === '') {
        $(this).keydown();
      }
    });

    $('.dataTable').on('keydown', 'input:not([data-new-row="false"], .form-control:not([data-new-row="false"])', function (e) {
      var o = dataTable.findPos($(this)), fiEvent,
              r = o[1],
              rows = parseInt($('.dataTable tbody tr:last').attr('aria-count'), 10);//$('.dataTable tbody tr').length;

      switch (e.which) {
        case kb.ENTER:
          fiEvent = $.Event('dataTable.row.creating');
          $(document).trigger(fiEvent, [this, o, rows]);
          if (fiEvent.isDefaultPrevented() === true) {
            return true;
          }
          if ($(dataTable.makeId('prod', r)).val() !== '') {
            dataTable.rowCreator(this, rows);
            break;
          }
          dataTable.rowCreator(this, rows);
          fiEvent = $.Event('dataTable.row.created');
          $(document).trigger(fiEvent, [this, o, rows]);
          break;
        case kb.ESC:
          $('.historyBoard').hide();
          break;
        default:
          break;
      }
    });

    var dtEvent = $.Event('dataTableInit');
    $(document).trigger(dtEvent);
  },
  refresh: function (ob) {
    "use strict";
    $('.dataTable').each(function (i, el) {
      $(el).find('tbody tr').each(function (ri, re) {
        $(re).find('[data-index="true"]').text(ri + 1);
      });
    });
    historyView(ob);
    var dtEvent = $.Event('dataTableRefresh');
    $(document).trigger(dtEvent, ob);
  },
  select: function (ui) {
    "use strict";
    //$(ui).parent('td').next($('td[aria-enabled="true"]')).find('.form-control').focus();
    /*$(ui).trigger('change');*/
    var dtEvent = $.Event('dataTableSelecting');
    $(document).trigger(dtEvent, this, ui);
    if (dtEvent.isDefaultPrevented() === true) {
      return true;
    }

    // dataTable.proceed(ui);
    // searchFormCtrl(ui);

  },
  proceed: function (ob) {
    "use strict";
    /*alert('proceed')*/

    var dtEvent = $.Event('dataTableNavigating');
    $(document).trigger(dtEvent, [ob, $(ob).data('focus')]);
    if (dtEvent.isDefaultPrevented() === true) {
      return true;
    }

    var tgt = '#' + $(ob).data('focus');

    if (tgt === '#undefined') {
      $('.dataTable').find('.form-control:first').focus().select();
      return false;
    }
    $(tgt).focus().select();
    if ($.trim($(tgt + '.batch').val()) !== '' ||
            $(tgt).is('[data-disabled=true]') ||
            $(tgt).is('[disabled]') ||
            $(tgt).is('[readonly]')) {
      // dataTable.proceed(tgt);
      // searchFormCtrl(tgt);
    } else {
      if ($(ob).has('.product') && $(ob).val() === '') {
        return false;
      }
      $(tgt).focus().select();
      //console.log('pointed >> ' + $(ob).data('pointed') +' >>> came >> '+ $(tgt).data('came'))
      //$(tgt).keydown();
      return false;
    }
  },
  prevCtrl: function (ob) {
    "use strict";
    var tgt = '#' + $(ob).data('came');
    /*tgt=$(tgt).data('pointed');*/
    $(tgt).focus().select();
    /*alert(tgt);*/
  },
  findPos: function (ob) {
    "use strict";
    /*
     Return Values
     -------------
     var o=findPos(this);
     - o[0] => ID prefix
     - o[1] => Row
     - o[2] => Column
     */
    var r = $(ob).attr('id').split('_r'),
            obj = r[0];
    /*,c=r[1].split('_c');*/
    r = r[1];
    /*c=c[1];*/
    return [obj, parseInt(r, 10)/*, parseInt(c)*/];
  },
  makeId: function (id, r) {
    "use strict";
    return ('#' + id + '_r' + r);
  },
  goToCtrl: function (id, r) {
    "use strict";
    $(dataTable.makeId(id, r)).focus().select();
  },
  rowCreator: function (ob, row) {
    "use strict";
    var dtEvent = $.Event('dataTableRowCreateIf');
    $(document).trigger(dtEvent, [ob, row]);
    if (dtEvent.isDefaultPrevented() === true) {
      return true;
    }

    var o = dataTable.findPos(ob);
    if (!(o[0] === 'prod' || o[0] === 'batch')) {/*o[0]==='qty' || o[0]==='rate' || o[0]==='val'*/
      if (o[1] === row) {
        dataTable.createNewRow(ob, row);
      }
      dataTable.goToCtrl('prod', o[1] + 1);
    }
  },
  createNewRow: function (ob, r) {
    "use strict";
    //console.log('newRow');
    var dtEvent = $.Event('dataTableRowCreating');
    if ($(ob).is('[data-new-row="false"]')) {
      return;
    }
    $(document).trigger(dtEvent, [ob, r]);
    if (dtEvent.isDefaultPrevented() === true) {
      return true;
    }

    var l = r += 1, //1+$('.dataTable tbody tr').length;
            //console.log('rows: '+l);
            row = $('.dataTable tbody tr:last').clone(true);

    $(row).attr('aria-count', r).find('td:first-child > span').html(l);

    $(row).find('.form-control').each(function (i, el) {
      $(el).val('');
      var id = $(el).attr('id'),
              foc = $(el).data('focus'),
              regx = /(_r[\d]+)/;

      $(this).removeClass('.autoCombo').attr("id", "").removeData().off();
      /*$(this).find('input').removeData().off();*/

      id = id.replace(regx, '_r' + l);
      if (typeof foc !== 'undefined') {
        foc = foc.replace(regx, '_r' + l);
      }
      $(el).attr({
        'id': id,
        'data-focus': foc,
        'aria-open': false
      });
      if ($(el).hasClass('date')) {
        $(el).removeClass('hasDatepicker').removeData().off();
        $(el).datepicker();
      }
    });

    $(row).appendTo('.dataTable tbody');

    //deletes the selected table rows
    // 	$(".dataTable .btn.delete").off('click.datatable.delete').on('click.datatable.delete', function (e) {
    //  $(this).off('click');
    //  e.stopPropagation();
    // 		var dtEvent = $.Event('dataTableRowDeleting');
    // 		$(document).trigger(dtEvent, [this, e]);
    // 		if (dtEvent.isDefaultPrevented() === true) { return true; }

    // 		if ($(this).parents('table').find('.delete').length > 1) {
    //    console.log('delete');
    // 			$(this).parents('tr').remove();
    // 		} else {
    // 			e.preventDefault();
    // 		}
    // 		dtEvent = $.Event('dataTableRowDeleted');
    //  dataTable.refresh();
    // 		$(document).trigger(dtEvent, [this, e]);
    // 	});

    var dtEvent = $.Event('dataTableRowCreated');
    $(document).trigger(dtEvent, [r]);
    if (dtEvent.isDefaultPrevented() === true) {
      return true;
    }
    $(".date").datepicker();
  }
};

var kb = {
	ESC: 27,
	TAB: 9,
	ENTER: 13,
	UP: 38,
	DOWN: 40,
	LEFT: 37,
	RIGHT: 39
};
/*! dataTable.js */
var autoCombo = {
	single: function (selector) {
		"use strict";
		/*
		SYNTAX
			autoCombo.single('.selectorName',
				{
					'selector':'.optSelectorName1',
					'src': SourceArray1
				},
				{
					'selector':'.optSelectorName2',
					'src': SourceArray2
				}
				...
			);
		*/
		var ob = selector,
			i = 0,
			vArgs = Array.prototype.slice.call(arguments, 1);

		$(ob).autocomplete({
			delay: 0,
			minLength: 0,
			source: [],
			autoFocus: true,
			open: function (event, ui) {

				$(this).attr('aria-open', true);
				if ($(this).is('[data-create]')) {
					$('.ui-autocomplete').append('<li tabindex="-1" class="link"><a data-toggle="modal" data-target="#' + $(this).data('create') + '" href="javascript:;">Create \'<span aria-creating=""></span>\'</a></li>');
					$(this).keyup(function () { $('.ui-autocomplete').find('[aria-creating]').html($(this).val()); });
				}
			},
			close: function () {
				$(this).attr('aria-open', false);
			},
			response: function (event, ui) {
				if ($(this).is('[data-create]')) {
					if (!ui.content.length) {
						ui.content.push({ "label": 'Create or Edit: "' + $(this).val() + '"', "value": 'Create or Edit' });
						//console.log(ui.content)
						//$("#message").text("No results found");
					} else {
						//console.log(ui.content)
						ui.content.pop();
						ui.content.push({ "label": 'Create or Edit: "' + $(this).val() + '"', "value": 'Create or Edit' });
						$("#message").empty();
					}
				}
			},
			change: function (event, ui) {
				if (ui.item === null) {
					$(this).val((ui.item ? ui.item.id : ''));
				}
			},
			select: function () {
				//$(this).trigger('change');
				dataTable.select(this);
			}
		});

		for (i = 0; i < vArgs.length; i += 1) {
			$(ob + vArgs[i].selector).autocomplete({
				source: vArgs[i].src
			});
		}
	},
	multiColumn: function (selector) {
		"use strict";
		/*
		SYNTAX
			autoCombo.multiColumn('.selectorName',
				{
					'selector':'.optSelectorName1',
					'src': SourceArray1
				},
				{
					'selector':'.optSelectorName2',
					'src': SourceArray2
				}
				...
			);
		*/
		var ob = selector,
			i = 0,
			vArgs = Array.prototype.slice.call(arguments, 1);

		$(ob).mcautocomplete({
			delay: 2,
			minLength: 0,
			showHeader: true,
			columns: [],
			source: [],
			autoFocus: true,
			open: function () {
				$(this).attr('aria-open', true);
				if ($(this).is('[data-create]')) {
					$('.ui-autocomplete').append('<li tabindex="-1" class="link"><a data-toggle="modal" data-target="#' + $(this).data('create') + '" href="javascript:;">Create \'<span aria-creating=""></span>\'</a></li>');
					$(this).keyup(function () { $('.ui-autocomplete').find('[aria-creating]').html($(this).val()); });
				}
				var wid = 20;
				$('.ui-menu:visible .ui-widget-header > span').each(function () { wid += $(this).width(); });
				$('.ui-menu:visible').width(wid + 18);
			},
			close: function () {
				$(this).attr('aria-open', false);
			},
			response: function (event, ui) {
				if ($(this).is('[data-create]')) {
					if (!ui.content.length) {
						ui.content.push({ "label": 'Create / Edit: "' + $(this).val() + '"', "value": $(this).val() })
						//console.log(ui.content)
						//$("#message").text("No results found");
					} else {
						ui.content.pop();
						ui.content.push({ "label": 'Create / Edit: "' + $(this).val() + '"', "value": $(this).val() })
						//$("#message").empty();
					}
				}
			},
			select: function (event, ui) {
				// Sets the input to the 'Make' column when item is selected.

				//console.log(this.value, ui.item.label, ui.item.label === /Create \/ Edit:/)

				//ui.item.label = (this.value === /Create \/ Edit:/)? ui.item.label: this.value;
				
				var o = dataTable.findPos(this), fiEvent,
					r = o[1];

				this.value = (ui.item ? ui.item.label : '');

				fiEvent = $.Event('mcautocombo.selecting');
				$(document).trigger(fiEvent, [this, ui]);
				if (fiEvent.isDefaultPrevented() === true) { return true; }

				
				//Needs to Manage with "mcautocombo.selecting" event_Trigger

				fiEvent = $.Event('mcautocombo.selected');
				$(this).trigger(fiEvent, [this, ui]);
				$(this).trigger('change');
				//if (fiEvent.isDefaultPrevented() === true) { return true; }

				//reInitList(this, '#packed_r' + r);
				return false;
			},
			change: function (event, ui) {
				var r = dataTable.findPos(this),
					arg = $(this).attr('data-update');
				r = r[1];
				if ($(this).val() !== $(this).attr('data-value')) {
					$(dataTable.makeId('pdetailid', r)).val('');
					switch (arg) {
						case 'server':
						case 'primary':
							$(this).addClass('text-primary');
							break;
						case 'client':
						case 'danger':
							$(this).addClass('text-danger');
							popup.open('#' + $(this).attr('data-dialog'), 768, 600, {
								units: 'px',
								xsource: $(this).attr('data-url'),
								setText: 'Create Product',
								setDataFocus: $(this).data('focus')
							});
							break;
						case 'prProductDialog':
							$(this).addClass('text-danger');
							prProductDialog($(this));
							break;
						default:
							$(this).removeClass('text-primary').removeClass('text-danger');
							break;
					}
					$(this).addClass('text-primary');
				} else {
					$(dataTable.makeId('pdetailid', r)).val($(dataTable.makeId('pdetailid', r)).attr('data-value'));
					$(this).removeClass('text-danger');
					$(this).removeClass('text-primary');
				}
			}
		});

		for (i = 0; i < vArgs.length; i += 1) {
			$(ob + vArgs[i].selector).mcautocomplete({
				columns: vArgs[i].columns,
				source: vArgs[i].src
			});
		}
	}
};

function valRound(n, r) {
	"use strict";
	r = Math.pow(10, parseInt(r, 10));
	r = (isNaN(r)) ? 100 : r;
	n = parseFloat(n);
	n = (isNaN(n)) ? 0 : n;
	n = $.isNumeric(n) ? Math.round(n * r) / r : n;
	return n;
}

/* DataTable */
$(function () {
	"use strict";
	dataTable.init();
	//$('.pack').mask('099X099X099');

	$('body').on('click', '.datepicker', function () {
		$(this).datepicker();
	});

	$(document).on('change focus', 'input[type="text"],.form-control', function () {
		var v = $(this).val();
		v = (isNaN(v)) ? '' : v;
		//$(this).val($.isNumeric($(this).val()) ? Math.round(v * 100) / 100 : $(this).val());
		historyView(this);
	});

	$('.dataTable').on('change focus', '.form-control', function () {
		var v = $(this).val();
		v = (isNaN(v)) ? '' : v;
		//$(this).val($.isNumeric($(this).val()) ? Math.round(v * 100) / 100 : $(this).val());

		dataTable.refresh(this);
		$(dataTable.makeId('disc', dataTable.findPos($(this))[1])).val($('#spDisc').val());

		$('[aria-table-sort="true"]').trigger("update");
	});

	$('.dataTable').on('change focus', '.qty.form-control', function () {
		if ($(this).val() === '' || $(this).val() <= 0) {
			$(this).val(1);
			$(this).select();
		}
		$(dataTable.makeId('free', dataTable.findPos(this)[1])).trigger('change');
	});
	$('.dataTable').on('change focus', '.free.form-control', function () {
		if ($(this).val() === '') {
			$(this).val(0);
		}
	});
	/*setTimeout(function(){
		loadJson('invoice','.dataTable');
	},2000);*/
	$(document).on('keydown', 'body', function (e) {
		switch (e.which) {
			case kb.ESC:
				$('.historyBoard').hide();
				$('.spotlightSearch').hide();
				break;
			case kb.ENTER:
				$('.spotlightSearch').hide();
				break;
			case 192:
				e.preventDefault();
				$('.spotlightSearch').show();
				break;
			default:
				//console.log(e.which)
				break;
		}
	});
});

function historyView(ob) {
	"use strict";
	if (!($(ob).is('[data-history]'))) {
		$('.historyBoard').hide();
		//$('#'+$(ob).data('history')+'-history').hide();
	} else {
		$('.historyBoard').hide();
		$('#' + $(ob).data('history') + '-history').show();
	}
}
