//var startCountDown = 1800
//var startTimeRemaining = 300;
var reduce = 1;
var handleDialog = null;
var handleTimerPage = null;
//var redirectPage = "#{request.contextPath}/login?reason=expired";
//var countDownDiv = "dialog-countdown";

function resetPageTimer() {
  PF('timeoutDialog').hide();
  clearInterval(handleDialog);
  clearInterval(handleTimerPage);
}

function startDialogTimer(wCounter, returnPage, countDownDiv) {
  var e = null;
  if (!e)
    e = document.getElementById(countDownDiv);
  e.innerHTML = wCounter;
  handleDialog = setInterval(function () {
    if (wCounter === 0) {
      clearInterval(handleDialog);
      window.location.href = returnPage;
    }
    else {
      wCounter -= reduce;
      e.innerHTML = wCounter;
    }
  }, reduce * 1000);
}

function startPageTimer(start, remaining, returnPage, timerDiv) {
  clearInterval(handleDialog);
  clearInterval(handleTimerPage);
  //startCountDown = start;

  handleTimerPage = setInterval(function () {
    if (start === remaining) {
      clearInterval(handleTimerPage);
      PF('timeoutDialog').show();  
      startDialogTimer(remaining, returnPage, timerDiv);
    }
    else {
      start -= reduce;
    }
  }, reduce * 1000);
}

