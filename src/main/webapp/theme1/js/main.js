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
    $('.layout').css('height', h-fh);
    
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
