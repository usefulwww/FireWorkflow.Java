function doSelectMe(id) {
	var theForm = document.getElementById('mainForm');
	var theSelectedWorkItemId = document.getElementById('selectedWorkItemId');

	var allRadioList = theForm.elements;

	for ( var i = 0; i < allRadioList.length; i++) {
		var item = allRadioList[i];

		if (item.type == "radio") {
			if (item.id != id) {
				item.checked = false;
			} else {
				theSelectedWorkItemId.value = item.value;
				item.checked = true;
			}
		}
	}
}