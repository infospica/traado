/*! multiAccordionMenu v1.0 | (c) 2015 [SDN Technologies] | MIT license
 The MIT License (MIT)
 Copyright (c) 2015
 Licence: https://github.com/asphub/multiAccordionMenu/blob/master/LICENSE
 */
$(document).ready(function(e) {(function($) {var allMenus = $('.sidebar ul.menu > li').toggleClass('active'); allMenus = $('.sidebar ul.menu > li').toggleClass('active'); $('.sidebar ul.menu > li').hover(function(fe){$(this).toggleClass('open'); fe.stopPropagation(); }); $('.sidebar ul.menu > li').click(function(fe) {allMenus.removeClass('active'); $('.sidebar ul.menu >li ul>li').removeClass('active'); $(this).toggleClass('active'); fe.stopPropagation(); return true; }); $('.toggle').click(function(fe){if ($(this).data('toggle') === $('.sidebar').data('name')){$('.sidebar').toggleClass('minimize'); }}); })(jQuery); });