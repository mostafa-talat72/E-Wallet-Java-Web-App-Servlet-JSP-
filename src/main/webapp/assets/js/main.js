(function () {
  "use strict";

  var APP = {
    toast: function (msg, type) {
      var stack = document.querySelector(".toast-stack");
      if (!stack) {
        stack = document.createElement("div");
        stack.className = "toast-stack";
        document.body.appendChild(stack);
      }
      var t = document.createElement("div");
      t.className = "toast-card" + (type === "error" ? " error" : "");
      var icon = type === "error" ? "error" : "success";
      t.innerHTML = '<i class="bi bi-check-circle-fill ' + icon + '"></i><span></span>';
      t.querySelector("span").textContent = msg;
      stack.appendChild(t);
      setTimeout(function () { t.classList.add("removing"); }, 2800);
      setTimeout(function () { t.remove(); }, 3150);
    },
    sel: function (s, c) { return (c || document).querySelectorAll(s); },
    one: function (s, c) { return (c || document).querySelector(s); }
  };
  window.APP = APP;

  function initSidebar() {
    var toggle = APP.one("#sidebarToggle");
    var overlay = APP.one("#sidebarOverlay");
    var sidebar = APP.one("#sidebar");
    if (!toggle || !sidebar) return;
    toggle.addEventListener("click", function () {
      sidebar.classList.toggle("open");
      if (overlay) overlay.classList.toggle("show");
    });
    if (overlay) overlay.addEventListener("click", function () {
      sidebar.classList.remove("open");
      overlay.classList.remove("show");
    });
  }

  function initTogglePin() {
    APP.sel("[data-toggle-pin]").forEach(function (btn) {
      btn.addEventListener("click", function () {
        var input = document.getElementById(btn.getAttribute("data-toggle-pin"));
        if (!input) return;
        var show = input.type === "password";
        input.type = show ? "text" : "password";
        btn.querySelector("i").className = show ? "bi bi-eye-slash" : "bi bi-eye";
      });
    });
    APP.sel("[data-toggle-otp]").forEach(function (btn) {
      btn.addEventListener("click", function () {
        var scope = btn.closest(".step-panel") || btn.closest(".otp-wrap");
        var boxes = scope ? scope.querySelectorAll("[data-otp]") : [];
        var show = false;
        boxes.forEach(function (b) { if (b.type === "password") show = true; });
        boxes.forEach(function (b) { b.type = show ? "text" : "password"; });
        var i = btn.querySelector("i");
        if (i) i.className = show ? "bi bi-eye-slash" : "bi bi-eye";
      });
    });
  }

  function maskDigits(input, max) {
    var limit = parseInt(input.getAttribute("data-max"), 10) || max;
    input.value = input.value.replace(/\D/g, "").slice(0, limit);
  }

  function initCardParts() {
    APP.sel("input[data-card-part]").forEach(function (part, i, arr) {
      part.addEventListener("input", function () {
        var v = part.value.replace(/\D/g, "").slice(0, 4);
        part.value = v;
        part.classList.toggle("has-value", v.length === 4);
        if (v.length === 4 && arr[i + 1]) arr[i + 1].focus();
      });
      part.addEventListener("keydown", function (e) {
        if (e.key === "Backspace" && !part.value && arr[i - 1]) {
          arr[i - 1].focus();
          arr[i - 1].value = "";
          arr[i - 1].classList.remove("has-value");
        }
        if ((e.key === "ArrowRight" || e.key === " ") && part.value && arr[i + 1]) {
          e.preventDefault();
          arr[i + 1].focus();
        }
        if (e.key === "ArrowLeft" && arr[i - 1]) {
          e.preventDefault();
          arr[i - 1].focus();
        }
      });
      part.addEventListener("paste", function (e) {
        e.preventDefault();
        var digits = (e.clipboardData || window.clipboardData).getData("text").replace(/\D/g, "").slice(0, 16);
        if (!digits) return;
        var start = i;
        digits.split("").forEach(function (d, k) {
          var box = arr[start + Math.floor(k / 4)];
          if (box) {
            box.value = d;
            box.classList.add("has-value");
          }
        });
        var lastBox = arr[start + Math.floor(Math.min(digits.length, 16) / 4) - 1] || arr[start];
        if (lastBox) lastBox.focus();
        arr.forEach(function (b, k) {
          if (k >= start && k < start + 4 && !b.value) b.classList.remove("has-value");
        });
      });
    });
  }

  function initExpirySelects() {
    APP.sel("select[data-exp-m]").forEach(function (mSel) {
      var ySel = mSel.closest(".d-flex").querySelector("select[data-exp-y]");
      if (!ySel) return;
      var phM = mSel.firstElementChild;
      var phY = ySel.firstElementChild;

      function build() {
        var now = new Date();
        var curYear = now.getFullYear();
        var curMonth = now.getMonth() + 1;
        var selM = parseInt(mSel.value, 10) || 0;
        var selY = parseInt(ySel.value, 10) || 0;

        var minYear = (selM && selM <= curMonth) ? curYear + 1 : curYear;
        var years = "";
        for (var y = minYear; y <= curYear + 8; y++) {
          years += '<option value="' + y + '">' + y + '</option>';
        }
        ySel.innerHTML = years;
        if (phY && phY.hasAttribute("disabled")) ySel.insertBefore(phY, ySel.firstChild);
        if (selY && selY >= minYear) ySel.value = String(selY);

        var maxM = selY === curYear ? curMonth : 0;
        var months = "";
        for (var m = maxM + 1; m <= 12; m++) {
          var label = (m < 10 ? "0" : "") + m;
          months += '<option value="' + label + '">' + label + '</option>';
        }
        mSel.innerHTML = months;
        if (phM && phM.hasAttribute("disabled")) mSel.insertBefore(phM, mSel.firstChild);
        var selMpad = selM && (maxM === 0 || selM > maxM) ? ((selM < 10 ? "0" : "") + selM) : "";
        mSel.value = selMpad;
      }

      mSel.addEventListener("change", build);
      ySel.addEventListener("change", build);
      build();
    });
  }

  function initMasks() {
    APP.sel("[data-phone]").forEach(function (el) {
      el.addEventListener("input", function () { maskDigits(el, 11); });
    });
    APP.sel("[data-card-input]").forEach(function (el) {
      el.addEventListener("input", function () {
        var v = el.value.replace(/\D/g, "").slice(0, 16);
        el.value = v.replace(/(.{4})/g, "$1 ").trim();
      });
    });
    APP.sel("[data-exp]").forEach(function (el) {
      el.addEventListener("input", function () {
        var v = el.value.replace(/[^\d]/g, "").slice(0, 4);
        if (v.length > 2) v = v.slice(0, 2) + "/" + v.slice(2);
        el.value = v;
      });
    });
    APP.sel("[data-cvv]").forEach(function (el) {
      el.addEventListener("input", function () { maskDigits(el, 3); });
    });
    APP.sel("[data-pin-input]").forEach(function (el) {
      el.addEventListener("input", function () { maskDigits(el, 6); });
    });
  }

  function initOtps() {
    APP.sel("[data-otp]").forEach(function (box) {
      box.addEventListener("input", function () {
        var v = box.value.replace(/\D/g, "");
        if (v.length > 1) {
          var vals = v.split("");
          var boxes = box.closest(".otp-row").querySelectorAll("[data-otp]");
          boxes.forEach(function (b, i) {
            if (vals[i]) { b.value = vals[i]; b.classList.add("has-value"); }
          });
          var last = boxes[Math.min(vals.length, boxes.length) - 1];
          if (last) last.focus();
          return;
        }
        if (v) box.classList.add("has-value");
        else box.classList.remove("has-value");
        var next = box.nextElementSibling;
        if (next && v) next.focus();
        checkOtpComplete(box.closest(".otp-row"));
      });
      box.addEventListener("keydown", function (e) {
        if (e.key === "Backspace" && !box.value) {
          var prev = box.previousElementSibling;
          if (prev) { prev.value = ""; prev.classList.remove("has-value"); prev.focus(); }
        }
      });
    });
  }

  function checkOtpComplete(row) {
    if (!row) return;
    var boxes = row.querySelectorAll("[data-otp]");
    return Array.prototype.every.call(boxes, function (b) { return b.value !== ""; });
  }

  function initOtpVerify() {
    APP.sel("[data-otp-verify]").forEach(function (btn) {
      btn.addEventListener("click", function () {
        var row = btn.closest("[data-stepper]").querySelector(".step-panel:not(.d-none) .otp-row");
        if (!row || !checkOtpComplete(row)) {
          APP.toast(APPMSG.invalid, "error");
          return;
        }
        var ok = false;
        var boxes = row.querySelectorAll("[data-otp]");
        var code = "";
        boxes.forEach(function (b) { code += b.value; });
        if (code === "123456") ok = true;
        if (!ok) {
          APP.toast(APPMSG.wrongPin, "error");
          return;
        }
        var stepper = btn.closest("[data-stepper]");
        var success = document.querySelector(btn.getAttribute("data-otp-verify"));
        stepper.classList.add("d-none");
        if (success) success.classList.remove("d-none");
        window.scrollTo({ top: 0, behavior: "smooth" });
      });
    });
  }

  function initCountdown(ctx) {
    var scope = ctx || document;
    APP.sel("[data-countdown]", scope).forEach(function (chip) {
      if (chip.dataset.started) return;
      chip.dataset.started = "1";
      var seconds = parseInt(chip.getAttribute("data-countdown"), 10) || 600;
      var until = chip.getAttribute("data-countdown-until");
      if (until) {
        var tUntil = new Date(until).getTime();
        if (!isNaN(tUntil)) {
          seconds = Math.max(0, Math.floor((tUntil - Date.now()) / 1000));
        }
      }
      var doneUrl = chip.getAttribute("data-countdown-url");
      var resend = document.querySelector("[data-resend]");
      function tick() {
        if (seconds <= 0) {
          if (doneUrl) {
            window.location.href = doneUrl;
            return;
          }
          chip.style.display = "none";
          if (resend) resend.style.display = "block";
          return;
        }
        var m = Math.floor(seconds / 60);
        var s = seconds % 60;
        var t = chip.querySelector("[data-countdown-time]");
        if (t) t.textContent = (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s;
        seconds--;
        setTimeout(tick, 1000);
      }
      tick();
    });
  }

  function initFeeCalc(stepper) {
    stepper.querySelectorAll("[data-fee-calc]").forEach(function (el) {
      var source = document.querySelector(el.getAttribute("data-fee-source") || "#amount");
      var target = el.getAttribute("data-fee-target") ? document.querySelector(el.getAttribute("data-fee-target")) : null;
      var rate = parseFloat(el.getAttribute("data-fee-rate")) || 0.001;
      var currency = el.getAttribute("data-fee-cur") || "";
      function update() {
        var amount = parseFloat(source ? source.value : 0);
        if (!(amount > 0)) {
          el.textContent = "0.00 " + currency;
          if (target) target.textContent = "—";
          return;
        }
        var fee = amount * rate;
        el.textContent = fee.toFixed(3) + " " + currency;
        if (target) target.textContent = (amount + fee).toFixed(3) + " " + currency;
      }
      update();
      if (source) source.addEventListener("input", update);
      stepper.querySelectorAll("[data-amount-chips]").forEach(function (row) {
        row.addEventListener("click", function () { update(); });
      });
    });
  }

  function fillPreviews(stepper) {
    stepper.querySelectorAll("[data-fill]").forEach(function (el) {
      var input = document.querySelector(el.getAttribute("data-fill"));
      if (input && input.value) {
        if (el.getAttribute("data-fill-mask") === "cc") {
          var n = input.value.replace(/\D/g, "");
          el.textContent = n.length >= 4 ? "•••• •••• •••• " + n.slice(-4) : "—";
        } else {
          el.textContent = input.value;
          if (input.type === "number") el.classList.add("amount-selected");
        }
      } else {
        el.textContent = "—";
        el.classList.remove("amount-selected");
      }
    });
  }

  function fillDonePanel(panel) {
    panel.querySelectorAll("[data-fill]").forEach(function (el) {
      var input = document.querySelector(el.getAttribute("data-fill"));
      if (input && input.value) {
        if (el.getAttribute("data-fill-mask") === "cc") {
          var n = input.value.replace(/\D/g, "");
          el.textContent = n.length >= 4 ? "•••• •••• •••• " + n.slice(-4) : "—";
        } else {
          el.textContent = input.value;
          if (input.type === "number") el.classList.add("amount-selected");
        }
      } else {
        el.textContent = "—";
        el.classList.remove("amount-selected");
      }
    });
  }

  function initSteps() {
    APP.sel("[data-stepper]").forEach(function (stepper) {
      var panels = Array.prototype.slice.call(stepper.querySelectorAll(".step-panel"));
      var dots = Array.prototype.slice.call(stepper.querySelectorAll("[data-step-dot]"));
      var current = 0;

      function show(i) {
        current = Math.max(0, Math.min(panels.length - 1, i));
        panels.forEach(function (p, idx) { p.classList.toggle("d-none", idx !== current); });
        dots.forEach(function (d, idx) {
          d.classList.toggle("active", idx === current);
          d.classList.toggle("done", idx < current);
        });
        if (panels[current]) {
          initCountdown(panels[current]);
          window.scrollTo({ top: panels[current].offsetTop - 100, behavior: "smooth" });
        }
      }

      function advance(panel) {
        if (panel && panel.querySelector("#saved-cards") && !panel.querySelector("#saved-cards .selected")) {
          APP.toast(APPMSG.selectCard, "error");
          return;
        }
        var bad = panel && panel.querySelector("input:invalid, select:invalid, textarea:invalid");
        if (bad) {
          if (bad.reportValidity) bad.reportValidity();
          APP.toast((bad.name || bad.id ? ("Missing: " + (bad.name || bad.id)) : APPMSG.invalid), "error");
          return;
        }
        var otpBox = panel && panel.querySelector(".otp-wrap .otp-row:not(.card-parts) [data-otp]");
        if (otpBox) {
          var otpRow = otpBox.closest(".otp-row");
          var boxes = otpRow.querySelectorAll("[data-otp]");
          var code = "";
          boxes.forEach(function (b) { code += b.value; });
          if (code.length < boxes.length) {
            APP.toast(APPMSG.invalid, "error");
            return;
          }
        }
        fillPreviews(stepper);
        show(current + 1);
      }
      stepper.advance = advance;

      stepper.querySelectorAll("[data-next]").forEach(function (btn) {
        btn.addEventListener("click", function () {
          advance(btn.closest(".step-panel"));
        });
      });
      stepper.querySelectorAll("[data-finish]").forEach(function (btn) {
        btn.addEventListener("click", function (e) {
          var form = btn.closest("form");
          if (form && form.getAttribute("action")) {
            return;
          }
          e.preventDefault();
          var ref = document.getElementById("ok-ref-num");
          if (ref) ref.textContent = Math.floor(100000 + Math.random() * 900000);
          fillPreviews(stepper);
          show(current + 1);
        });
      });
      stepper.querySelectorAll("[data-prev]").forEach(function (btn) {
        btn.addEventListener("click", function () { show(current - 1); });
      });
      initFeeCalc(stepper);
      show(0);
    });
  }

  function initSavedCards() {
    var fields = ["#add-card-number", "#add-label", "#add-holder", "#add-cvv"];
    function setLocked(locked) {
      fields.forEach(function (sel) {
        document.querySelectorAll(sel).forEach(function (el) {
          if (el.type !== "hidden") {
            el.disabled = locked;
          }
          el.classList.toggle("card-fields-locked", locked);
        });
      });
    }
    function clearSelection() {
      document.querySelectorAll("#saved-cards [data-saved-card]").forEach(function (c) {
        c.classList.remove("selected");
      });
      var hnum = document.getElementById("add-card-number");
      if (hnum) hnum.value = "";
      var label = document.getElementById("add-label");
      if (label) label.value = "";
      var holder = document.getElementById("add-holder");
      if (holder) holder.value = "";
      var cvv = document.getElementById("add-cvv");
      if (cvv) cvv.value = "";
      setLocked(false);
      var cancel = document.querySelector("[data-cancel-card]");
      if (cancel) cancel.classList.add("d-none");
    }
    APP.sel("[data-saved-card]").forEach(function (card) {
      card.addEventListener("click", function () {
        if (card.classList.contains("selected")) {
          clearSelection();
          return;
        }
        clearSelection();
        card.classList.add("selected");
        var num = (card.getAttribute("data-number") || "").replace(/\s/g, "");
        var hnum = document.getElementById("add-card-number");
        if (hnum) hnum.value = num;
        var label = document.getElementById("add-label");
        if (label) label.value = card.getAttribute("data-label") || "";
        var holder = document.getElementById("add-holder");
        if (holder) holder.value = card.getAttribute("data-holder") || "";
        setLocked(true);
        var cancel = document.querySelector("[data-cancel-card]");
        if (cancel) cancel.classList.remove("d-none");
        var cvv = document.getElementById("add-cvv");
        if (cvv) cvv.value = card.getAttribute("data-cvv") || "";
      });
    });
    APP.sel("[data-cancel-card]").forEach(function (btn) {
      btn.addEventListener("click", clearSelection);
    });
  }

  function initQuickAmounts() {
    APP.sel("[data-amount-chips]").forEach(function (row) {
      row.addEventListener("click", function (e) {
        var chip = e.target.closest("[data-value]");
        if (!chip) return;
        row.querySelectorAll(".chip").forEach(function (c) { c.classList.remove("selected"); });
        chip.classList.add("selected");
        var target = document.getElementById(row.getAttribute("data-amount-chips"));
        if (target) target.value = chip.getAttribute("data-value");
      });
    });
  }

  function initTxFilters() {
    var list = APP.one("[data-tx-list]");
    if (!list) return;
    var searches = Array.prototype.slice.call(APP.sel("[data-tx-search]"));
    APP.sel("[data-filter-pill]").forEach(function (pill) {
      pill.addEventListener("click", function () {
        APP.sel("[data-filter-pill]").forEach(function (p) { p.classList.remove("active"); });
        pill.classList.add("active");
        applyTxFilter(getSearchValue(), pill.getAttribute("data-filter-pill"));
      });
    });
    function getSearchValue() {
      var v = "";
      searches.forEach(function (s) { if (s.value) v = s.value; });
      return v;
    }
    searches.forEach(function (search) {
      search.addEventListener("input", function () {
        var active = APP.one("[data-filter-pill].active");
        applyTxFilter(getSearchValue(), active ? active.getAttribute("data-filter-pill") : "all");
      });
    });
    function typeMatches(type, filter) {
      if (filter === "all") return true;
      return type === filter;
    }
    function applyTxFilter(text, filter) {
      var visible = 0;
      list.querySelectorAll("[data-tx-row]").forEach(function (row) {
        var type = row.getAttribute("data-type");
        var amount = parseFloat(row.getAttribute("data-amount")) || 0;
        var hay = (row.textContent || "").toLowerCase();
        var matchesFilter = filter === "receive" ? amount > 0 : typeMatches(type, filter);
        var matchesText = !text || hay.indexOf(text.toLowerCase()) !== -1;
        var show = matchesFilter && matchesText;
        row.style.display = show ? "" : "none";
        if (show) visible++;
      });
      var empty = list.querySelector(".empty-no-tx");
      if (empty) empty.style.display = visible === 0 ? "" : "none";
    }
  }

  function initNotif() {
    APP.sel("[data-mark-all]").forEach(function (btn) {
      btn.addEventListener("click", function () {
        APP.sel(".notif-item.unread", document.querySelector("[data-notif-list]") || document).forEach(function (n) {
          n.classList.remove("unread");
        });
        APP.toast(APPMSG.marked);
      });
    });
    APP.sel("[data-notif-list]").forEach(function (list) {
      list.querySelectorAll(".notif-item").forEach(function (item) {
        item.addEventListener("click", function () {
          if (item.classList.contains("unread")) {
            item.classList.remove("unread");
            APP.toast(APPMSG.marked);
          }
        });
      });
    });
    APP.sel("[data-del-notif]").forEach(function (btn) {
      btn.addEventListener("click", function (e) {
        e.stopPropagation();
        btn.closest(".notif-item").remove();
        APP.toast(APPMSG.removed);
      });
    });
  }

  function initCopy() {
    APP.sel("[data-copy]").forEach(function (btn) {
      btn.addEventListener("click", function () {
        var txt = btn.getAttribute("data-copy");
        if (navigator.clipboard && navigator.clipboard.writeText) {
          navigator.clipboard.writeText(txt).then(function () { APP.toast(APPMSG.copied); });
        } else {
          APP.toast(APPMSG.copied);
        }
      });
    });
  }

  function initPinStrength() {
    APP.sel("[data-pin-meter]").forEach(function (input) {
      var meter = APP.one("[data-pin-meter-bars]");
      var label = APP.one("[data-pin-meter-label]");
      input.addEventListener("input", function () {
        var v = input.value;
        var score = 0;
        if (/^(?!#)/.test(v) && v.length > 0) score += 1;
        if (/^(?=.*\d)/.test(v) && v.length >= 4) score += 1;
        if (/(.)\1{2,}/.test(v) || /0123|1234|2345|3456|4567|5678|6789|7890|9876|8765|7654|6543|5432|4321|3210/.test(v)) score -= 1;
        score = Math.max(0, score);
        if (meter) {
          Array.prototype.forEach.call(meter.children, function (bar, i) {
            bar.className = i < score ? (score === 1 ? "on-weak" : score === 2 ? "on-medium" : "on-strong") : "";
          });
        }
        if (label) {
          label.textContent = score === 0 ? "" : score === 1 ? APPMSG.weak : score === 2 ? APPMSG.medium : APPMSG.strong;
        }
      });
    });
  }

  function initBalanceToggle() {
    var btn = APP.one("[data-balance-toggle]");
    if (!btn) return;
    btn.addEventListener("click", function () {
      var el = APP.one("[data-balance-value]");
      var icon = btn.querySelector("i");
      if (el.getAttribute("data-hidden") !== "1") {
        el.setAttribute("data-hidden", "1");
        el.textContent = "••••••";
        icon.className = "bi bi-eye";
      } else {
        el.setAttribute("data-hidden", "0");
        el.textContent = el.getAttribute("data-full");
        icon.className = "bi bi-eye-slash";
      }
    });
  }

  function initChart() {
    var el = APP.one("#cashflowChart");
    if (!el || !window.Chart) return;
    var incomeLabel = el.getAttribute("data-income") || "Income";
    var expenseLabel = el.getAttribute("data-expense") || "Expenses";
    new Chart(el.getContext("2d"), {
      type: "line",
      data: {
        labels: [el.getAttribute("data-d1"), el.getAttribute("data-d2"), el.getAttribute("data-d3"), el.getAttribute("data-d4"), el.getAttribute("data-d5"), el.getAttribute("data-d6"), el.getAttribute("data-d7")],
        datasets: [
          {
            label: incomeLabel,
            data: [1200, 1800, 900, 2100, 1300, 2600, 1500],
            borderColor: "#16a34a",
            backgroundColor: "rgba(22,163,74,.12)",
            fill: true,
            tension: .45,
            borderWidth: 3,
            pointRadius: 4,
            pointBackgroundColor: "#16a34a"
          },
          {
            label: expenseLabel,
            data: [620, 480, 980, 700, 850, 520, 660],
            borderColor: "#2563eb",
            backgroundColor: "rgba(37,99,235,.1)",
            fill: true,
            tension: .45,
            borderWidth: 3,
            pointRadius: 4,
            pointBackgroundColor: "#2563eb"
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { mode: "index", intersect: false },
        plugins: {
          legend: {
            labels: { usePointStyle: true, padding: 20, font: { family: "Cairo", weight: "bold", size: 12 } }
          }
        },
        scales: {
          y: { beginAtZero: true, grid: { color: "#eef1f8" }, border: { display: false }, ticks: { font: { family: "Cairo" } } },
          x: { grid: { display: false }, border: { display: false }, ticks: { font: { family: "Cairo" } } }
        }
      }
    });
  }

  function initMethodTabs() {
    APP.sel("[data-method-tab]").forEach(function (tab) {
      tab.addEventListener("click", function () {
        var group = tab.closest(".btn-group-tab");
        group.querySelectorAll("[data-method-tab]").forEach(function (t) { t.classList.remove("active"); });
        tab.classList.add("active");
        var target = tab.getAttribute("data-method-tab");
        APP.sel("[data-method-panel]").forEach(function (p) {
          var js = p.getAttribute("data-method-panel");
          p.classList.toggle("d-none", js !== target);
        });
      });
    });
  }

  function initContactChips() {
    APP.sel("[data-contact]").forEach(function (chip) {
      chip.addEventListener("click", function () {
        var target = document.getElementById(chip.getAttribute("data-contact"));
        if (target) target.value = chip.getAttribute("data-number");
      });
    });
  }

  function bindCardWidget(widget) {
    var sw = widget.querySelector("[data-card-toggle]");
    if (sw) {
      sw.addEventListener("change", function () {
        var badge = widget.querySelector("[data-card-status]");
        if (!badge) return;
        if (sw.checked) {
          badge.className = "badge badge-success";
          badge.innerHTML = '<i class="bi bi-check-circle"></i> ' + (window.APPMSG ? APPMSG.active : "Active");
        } else {
          badge.className = "badge badge-neutral";
          badge.textContent = window.APPMSG ? APPMSG.inactive : "Inactive";
        }
        var form = widget.querySelector("[data-card-toggle-form]");
        if (form) {
          var st = form.querySelector('input[name="status"]');
          if (st) st.value = sw.checked ? "1" : "0";
          form.submit();
        }
      });
    }
    var del = widget.querySelector("[data-delete-card]");
    if (del) {
      del.addEventListener("click", function () {
        var id = document.getElementById("delCardId");
        if (id) id.value = del.getAttribute("data-card-id") || "";
        function fill(elId, val) {
          var el = document.getElementById(elId);
          if (el) el.textContent = val || "—";
        }
        fill("delCardLabel", del.getAttribute("data-card-label"));
        fill("delCardHolder", del.getAttribute("data-card-holder"));
        fill("delCardBank", del.getAttribute("data-card-bank"));
        fill("delCardExpire", del.getAttribute("data-card-expire"));
        var num = del.getAttribute("data-card-number") || "";
        if (num) num = num.slice(0, 4) + " •••• •••• " + num.slice(-4);
        fill("delCardNumber", num);
        var modalEl = document.getElementById("deleteCardModal");
        if (modalEl) {
          var modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
          modal.show();
        }
      });
    }
  }

  function initCardModals() {
    if (/[?&]addCard=1/.test(window.location.search)) {
      var autoModal = document.getElementById("addCardModal");
      if (autoModal) {
        var autoM = bootstrap.Modal.getInstance(autoModal) || new bootstrap.Modal(autoModal);
        autoM.show();
      }
    }
    APP.sel("[data-add-card]").forEach(function (btn) {
      btn.addEventListener("click", function () {
        var form = document.getElementById("card-add-form");
        if (form) {
          if (!form.checkValidity()) {
            form.reportValidity();
            APP.toast(APPMSG.invalid, "error");
            return;
          }
          var parts = form.querySelectorAll("[data-card-part]");
          var number = "";
          parts.forEach(function (p) { number += p.value; });
          var name = form.name.value.trim();
          var bank = form.bank.value.trim();
          var label = (form.label ? form.label.value.trim() : "") || bank;
          var expM = form.expMonth.value;
          var expY = form.expYear.value;
          var expire = (expM ? ("0" + expM).slice(-2) : "MM") + "/" + (expY ? String(expY).slice(-2) : "YY");
          var tones = ["blue", "violet", "emerald"];
          var grid = document.getElementById("cards-grid");
          var count = grid ? grid.querySelectorAll("[data-card-widget]").length : 0;
          var tone = tones[count % tones.length];
          var widget = document.createElement("div");
          widget.className = "col-12 col-md-6 col-xl-4";
          widget.setAttribute("data-card-widget", "");
          widget.innerHTML =
            '<div class="bank-card theme-' + tone + '">' +
              '<div class="card-bg"></div>' +
              '<div class="card-top">' +
                '<span class="card-brand"><i class="bi bi-wallet2"></i> E-Wallet</span>' +
                '<span class="badge badge-white">' + label + '</span>' +
              '</div>' +
              '<div class="card-number" style="direction:ltr">' + number.slice(0, 4) + ' •••• •••• ' + number.slice(-4) + '</div>' +
              '<div class="card-bottom">' +
                '<div class="card-holder">' +
                  '<small>' + (APPMSG.cardsHolder || "Cardholder") + '</small>' +
                  '<strong>' + name + '</strong>' +
                '</div>' +
                '<div class="text-end">' +
                  '<small class="d-block opacity-75" style="font-size:.62rem">' + (APPMSG.cardsExpires || "Expires") + '</small>' +
                  '<strong style="font-family:monospace;letter-spacing:1px">' + expire + '</strong>' +
                '</div>' +
              '</div>' +
            '</div>' +
            '<div class="d-flex align-items-center justify-content-between mt-3">' +
              '<span class="badge badge-success" data-card-status><i class="bi bi-check-circle"></i> ' + (APPMSG.active || "Active") + '</span>' +
              '<div class="d-flex gap-2">' +
              '<form class="m-0" action="cardController?action=updateCardStatus" method="post" data-card-toggle-form>' +
                '<input type="hidden" name="cardId" value="">' +
                '<input type="hidden" name="status" value="1">' +
                '<label class="form-check form-switch m-0" title="toggle">' +
                  '<input class="form-check-input" type="checkbox" data-card-toggle checked>' +
                '</label>' +
              '</form>' +
              '<button type="button" class="btn btn-danger-soft btn-icon-sm" data-delete-card data-card-id="" data-card-number="' + number + '" data-card-label="' + (label || "") + '" data-card-holder="' + name + '" data-card-bank="' + bank + '" data-card-expire="' + expire + '">' +
                '<i class="bi bi-trash"></i>' +
              '</button>' +
            '</div>';
          if (grid) {
            grid.insertBefore(widget, grid.firstChild);
            bindCardWidget(widget);
          }
          form.reset();
        }
        var modalEl = document.getElementById("addCardModal");
        if (modalEl) {
          var modal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl);
          modal.hide();
        }
        APP.toast(APPMSG.cardAdded);
      });
    });
    APP.sel("[data-card-widget]").forEach(bindCardWidget);
  }

  function initProfileDelete() {
    var form = document.getElementById("delete-form");
    var pin = document.getElementById("delPin");
    var phone = document.getElementById("delPhone");
    if (!form || !pin) return;
    form.addEventListener("submit", function (e) {
      if (!phone || phone.value.length !== 11 || !pin.value || pin.value.length !== 6) {
        e.preventDefault();
        APP.toast(APPMSG.invalid, "error");
        if (phone && phone.value.length !== 11) phone.focus();
        else pin.focus();
        return;
      }
    });
  }

  document.addEventListener("DOMContentLoaded", function () {
    initSidebar();
    initTogglePin();
    initMasks();
    initCardParts();
    initExpirySelects();
    initSavedCards();
    initOtps();
    initCountdown(document);
    initSteps();
    initQuickAmounts();
    initTxFilters();
    initNotif();
    initCopy();
    initPinStrength();
    initBalanceToggle();
    initChart();
    initMethodTabs();
    initContactChips();
    initOtpVerify();
    initCardModals();
    initProfileDelete();
    APP.sel("[data-done]").forEach(function (p) {
      if (!p.classList.contains("d-none")) fillDonePanel(p);
    });
  });
})();