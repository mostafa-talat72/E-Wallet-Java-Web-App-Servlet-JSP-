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
  }

  function maskDigits(input, max) {
    var limit = parseInt(input.getAttribute("data-max"), 10) || max;
    input.value = input.value.replace(/\D/g, "").slice(0, limit);
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
      el.addEventListener("input", function () { maskDigits(el, 4); });
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
        if (code === "1234") ok = true;
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
      var resend = document.querySelector("[data-resend]");
      function tick() {
        if (seconds <= 0) {
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

  function fillPreviews(stepper) {
    stepper.querySelectorAll("[data-fill]").forEach(function (el) {
      var input = document.querySelector(el.getAttribute("data-fill"));
      el.textContent = input && input.value ? input.value : "—";
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

      stepper.querySelectorAll("[data-next]").forEach(function (btn) {
        btn.addEventListener("click", function () {
          var panel = btn.closest(".step-panel");
          var form = panel && panel.querySelector(".validates");
          if (form) {
            if (!form.checkValidity()) {
              form.reportValidity();
              APP.toast(APPMSG.invalid, "error");
              return;
            }
          }
          fillPreviews(stepper);
          show(current + 1);
        });
      });
      stepper.querySelectorAll("[data-prev]").forEach(function (btn) {
        btn.addEventListener("click", function () { show(current - 1); });
      });
      show(0);
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
      if (filter === "income") return type === "deposit";
      if (filter === "expense") return type === "transfer" || type === "withdraw" || type === "payment";
      return type === filter;
    }
    function applyTxFilter(text, filter) {
      var visible = 0;
      list.querySelectorAll("[data-tx-row]").forEach(function (row) {
        var type = row.getAttribute("data-type");
        var hay = (row.textContent || "").toLowerCase();
        var matchesFilter = typeMatches(type, filter);
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

  function initDeleteConfirm() {
    APP.sel("[data-delete-confirm]").forEach(function (btn) {
      btn.addEventListener("click", function (e) {
        var msg = btn.getAttribute("data-delete-confirm");
        if (typeof confirm === "function" && !window.confirm(msg)) {
          e.preventDefault();
          return;
        }
        var card = btn.closest("[data-card-widget]");
        if (card) {
          btn.removeEventListener("click", this);
          card.remove();
          APP.toast(APPMSG.cardRemoved);
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

  function initAddConfirm() {
    APP.sel("[data-add-confirm]").forEach(function (btn) {
      btn.addEventListener("click", function () {
        var amount = document.getElementById("amount");
        if (!amount || !amount.value || parseFloat(amount.value) <= 0) {
          APP.toast(APPMSG.invalid, "error");
          if (amount) amount.focus();
          return;
        }
        var form = document.getElementById("card-form");
        if (form && !form.checkValidity()) {
          form.reportValidity();
          APP.toast(APPMSG.invalid, "error");
          return;
        }
        fillPreviews(document);
        var cardPanel = document.getElementById("card-panel");
        var amountPanel = document.getElementById("amount-panel");
        if (cardPanel) cardPanel.classList.add("d-none");
        if (amountPanel) amountPanel.classList.add("d-none");
        var method = document.getElementById("ok-method");
        var lbl = document.getElementById("ok-method-label");
        if (method && lbl) method.textContent = lbl.textContent.trim();
        var success = document.querySelector("#add-success");
        if (success) success.classList.remove("d-none");
        window.scrollTo({ top: 0, behavior: "smooth" });
        APP.toast(APPMSG.saved);
      });
    });
  }

  function initCardModals() {
    APP.sel("[data-add-card]").forEach(function (btn) {
      btn.addEventListener("click", function () {
        var form = document.getElementById("card-add-form");
        if (form) {
          if (!form.checkValidity()) {
            form.reportValidity();
            APP.toast(APPMSG.invalid, "error");
            return;
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
    APP.sel("[data-card-toggle]").forEach(function (sw) {
      sw.addEventListener("change", function () {
        var widget = sw.closest("[data-card-widget]");
        if (!widget) return;
        var badge = widget.querySelector(".badge");
        if (sw.checked) {
          badge.className = "badge badge-success";
          badge.innerHTML = '<i class="bi bi-check-circle"></i> ' + (window.APPMSG ? APPMSG.active : "Active");
        } else {
          badge.className = "badge badge-neutral";
          badge.textContent = window.APPMSG ? APPMSG.inactive : "Inactive";
        }
      });
    });
  }

  function initSaveForms() {
    APP.sel("[data-save-form]").forEach(function (btn) {
      btn.addEventListener("click", function () {
        var form = document.getElementById(btn.getAttribute("data-save-form"));
        if (!form) return;
        if (!form.checkValidity()) {
          form.reportValidity();
          APP.toast(APPMSG.invalid, "error");
          return;
        }
        var pinForm = btn.closest("#pin-form");
        if (pinForm) {
          var np = pinForm.querySelector("[name=newPin]");
          var np2 = pinForm.querySelector("[name=newPin2]");
          if (np.value !== np2.value) {
            APP.toast(APPMSG.invalid, "error");
            return;
          }
          pinForm.reset();
          APP.toast(APPMSG.pinChanged);
          return;
        }
        form.reset();
        APP.toast(APPMSG.saved);
      });
    });
  }

  document.addEventListener("DOMContentLoaded", function () {
    initSidebar();
    initTogglePin();
    initMasks();
    initOtps();
    initSteps();
    initQuickAmounts();
    initTxFilters();
    initNotif();
    initCopy();
    initDeleteConfirm();
    initPinStrength();
    initBalanceToggle();
    initChart();
    initMethodTabs();
    initContactChips();
    initOtpVerify();
    initAddConfirm();
    initCardModals();
    initSaveForms();
  });
})();