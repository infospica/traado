$(document).on('keydown', function (e) {
  if ((e.ctrlKey || e.metaKey) && e.keyCode == 83) {
    e.preventDefault();
    hotkeySave();
  }
});


