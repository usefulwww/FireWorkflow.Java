var defaultDataTableId = "";

function controlOnMouseOver(theImg) {
	theImg.style.border = "1px outset silver";
}
function controlOnMouseOut(theImg) {
	theImg.style.border = "0px outset silver";
}
function selectRow(row) {
	try {
		var theTable = row.parentNode.parentNode;
		var oldRow = theTable.currentRow;
		if (oldRow != null) {
			oldRow.className = oldRow.oldClassName;
		}
		theTable.currentRow = row;
		row.oldClassName = row.className;
		row.className = "dataTableRowSelected";

		var firstCell = row.firstChild;
		var allElm = firstCell.childNodes;
		var elm = null;

		for ( var i = 0; i < allElm.length; i++) {
			elm = allElm.item(i);
			
			if ((typeof elm.name) != "undefined" && elm.name=="RowActionAnchor") {
				//alert(elm.href);
				elm.click();
				break;
			}
		}
	} catch (e) {
		alert(e.message);
	}
}

function scrollRow(table) {
	if (event.keyCode == 38) {//up
		var theTable = table;
		var oldRow = theTable.currentRow;
		if (oldRow != null) {
			var preRow = oldRow.previousSibling;
			if (preRow != null) {
				if (preRow.style.display != "none") {
					preRow.click();
					oldRow.className = oldRow.oldClassName;
				} else {
					prePage(table.id);
				}
			}
		} else {
			tBody = theTable.tBodies.item(0);
			var preRow = tBody.firstChild;
			if (preRow != null) {
				if (preRow.style.display != "none") {
					preRow.click();
				}
			}
		}
	} else if (event.keyCode == 40) {//down
		var theTable = table;
		var oldRow = theTable.currentRow;
		// alert(oldRow);
		if (oldRow != null) {
			var nextRow = oldRow.nextSibling;
			if (nextRow != null) {
				if (nextRow.style.display != "none") {
					nextRow.click();
					oldRow.className = oldRow.oldClassName;
				} else {
					nextPage(table.id);
				}

			}

		} else {
			tBody = theTable.tBodies.item(0);
			var nextRow = tBody.firstChild;
			if (nextRow != null && nextRow.style.display != "none") {
				nextRow.click();
			}
		}
	} else if (event.keyCode == 33) {//pageup

		prePage(table.id);
	} else if (event.keyCode == 34) {//pagedown

		nextPage(table.id);
	}

}

function firstPage(tableId) {
	var theCurrentPageInput = document.getElementById("currentPage_4_"
			+ tableId);
	if (new Number(theCurrentPageInput.value) > 1) {
		theCurrentPageInput.value = 1;
	}
	var firstPageControl = document.getElementById("firstPageControl_4_"
			+ tableId);
	var prePageControl = document.getElementById("prePageControl_4_" + tableId);
	var nextPageControl = document.getElementById("nextPageControl_4_"
			+ tableId);
	var lastPageControl = document.getElementById("lastPageControl_4_"
			+ tableId);
	firstPageControl.src = firstPageControl.imageFolder
			+ "page-first-disabled.gif";
	prePageControl.src = prePageControl.imageFolder + "page-prev-disabled.gif";
	nextPageControl.src = nextPageControl.imageFolder + "page-next.gif";
	lastPageControl.src = lastPageControl.imageFolder + "page-last.gif";

	var table = theCurrentPageInput.parentNode.parentNode.parentNode.parentNode;
	gotoPage(1, table, theCurrentPageInput.value);
}
function prePage(tableId) {
	var theCurrentPageInput = document.getElementById("currentPage_4_"
			+ tableId);
	if (new Number(theCurrentPageInput.value) > 1) {
		theCurrentPageInput.value = new Number(theCurrentPageInput.value) - 1;
	}

	var firstPageControl = document.getElementById("firstPageControl_4_"
			+ tableId);
	var prePageControl = document.getElementById("prePageControl_4_" + tableId);
	var nextPageControl = document.getElementById("nextPageControl_4_"
			+ tableId);
	var lastPageControl = document.getElementById("lastPageControl_4_"
			+ tableId);

	if (theCurrentPageInput.value == 1) {
		firstPageControl.src = firstPageControl.imageFolder
				+ "page-first-disabled.gif";
		prePageControl.src = prePageControl.imageFolder
				+ "page-prev-disabled.gif";
	} else {
		firstPageControl.src = firstPageControl.imageFolder + "page-first.gif";
		prePageControl.src = prePageControl.imageFolder + "page-prev.gif";
	}
	nextPageControl.src = nextPageControl.imageFolder + "page-next.gif";
	lastPageControl.src = lastPageControl.imageFolder + "page-last.gif";

	var table = theCurrentPageInput.parentNode.parentNode.parentNode.parentNode;
	gotoPage(1, table, theCurrentPageInput.value);
}

function nextPage(tableId) {
	var theCurrentPageInput = document.getElementById("currentPage_4_"
			+ tableId);
	var totalPage = theCurrentPageInput.totalPage;
	if (new Number(theCurrentPageInput.value) < new Number(totalPage)) {
		theCurrentPageInput.value = new Number(theCurrentPageInput.value) + 1;
	}

	var firstPageControl = document.getElementById("firstPageControl_4_"
			+ tableId);
	var prePageControl = document.getElementById("prePageControl_4_" + tableId);
	var nextPageControl = document.getElementById("nextPageControl_4_"
			+ tableId);
	var lastPageControl = document.getElementById("lastPageControl_4_"
			+ tableId);

	firstPageControl.src = firstPageControl.imageFolder + "page-first.gif";
	prePageControl.src = prePageControl.imageFolder + "page-prev.gif";
	if (theCurrentPageInput.value == totalPage) {
		nextPageControl.src = nextPageControl.imageFolder
				+ "page-next-disabled.gif";
		lastPageControl.src = lastPageControl.imageFolder
				+ "page-last-disabled.gif";
	} else {
		nextPageControl.src = nextPageControl.imageFolder + "page-next.gif";
		lastPageControl.src = lastPageControl.imageFolder + "page-last.gif";
	}

	var table = theCurrentPageInput.parentNode.parentNode.parentNode.parentNode;
	gotoPage(2, table, theCurrentPageInput.value);
}

function lastPage(tableId) {
	var theCurrentPageInput = document.getElementById("currentPage_4_"
			+ tableId);
	var totalPage = theCurrentPageInput.totalPage;
	if (new Number(theCurrentPageInput.value) < new Number(totalPage)) {
		theCurrentPageInput.value = new Number(totalPage);
	}

	var firstPageControl = document.getElementById("firstPageControl_4_"
			+ tableId);
	var prePageControl = document.getElementById("prePageControl_4_" + tableId);
	var nextPageControl = document.getElementById("nextPageControl_4_"
			+ tableId);
	var lastPageControl = document.getElementById("lastPageControl_4_"
			+ tableId);

	firstPageControl.src = firstPageControl.imageFolder + "page-first.gif";
	prePageControl.src = prePageControl.imageFolder + "page-prev.gif";

	nextPageControl.src = nextPageControl.imageFolder
			+ "page-next-disabled.gif";
	lastPageControl.src = lastPageControl.imageFolder
			+ "page-last-disabled.gif";

	var table = theCurrentPageInput.parentNode.parentNode.parentNode.parentNode;
	gotoPage(2, table, theCurrentPageInput.value);
}

function gotoPage(flag, table, pageNumber) {
	//flag==1,means prevous; flag==2 means next;
	var theTable = table;

	var tBody = theTable.tBodies.item(0);
	if (tBody != null) {
		var rows = tBody.childNodes;

		var row = null;
		var findFirstRow = false;
		if (flag == 2) {
			for ( var i = 0; i < rows.length; i++) {
				row = rows.item(i);
				if (row.pageIndex == pageNumber) {
					row.style.display = "block";
					if (!findFirstRow) {
						row.click();
						findFirstRow = true;
					}
				} else {
					row.style.display = "none";
				}

			}
		} else {
			for ( var i = rows.length - 1; i >= 0; i--) {
				row = rows.item(i);
				if (row.pageIndex == pageNumber) {
					row.style.display = "block";
					if (!findFirstRow) {
						row.click();
						findFirstRow = true;
					}
				} else {
					row.style.display = "none";
				}

			}
		}
	}
}

function selectTheFirstRow() {
	var theTable = document.getElementById(defaultDataTableId);
	var tBody = theTable.tBodies.item(0);
	var preRow = tBody.firstChild;
	if (preRow != null) {
		if (preRow.style.display != "none") {
			preRow.click();
		}
	}
}