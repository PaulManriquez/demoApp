// ================= EDIT MODAL =================
function showEditBranchModal(name, address, mapsLink, id) {

    document.getElementById('branchModalName').value = name;
    document.getElementById('branchModalAddress').value = address;
    document.getElementById('branchModalMapsLink').value = mapsLink;
    document.getElementById('branchIdModal').value = id;

    // Bootstrap 5 way                            This html front end is being called
    const modalElement = document.getElementById('editBranchModal');
    const modal = new bootstrap.Modal(modalElement);
    modal.show();
}

// ================= CONFIRMATION MODAL =================
function showConfirmationModal(text, link, action, btnClass) {

    document.getElementById('confirmationModalLabel').innerText = action;
    document.getElementById('confirmationModalContent').innerText = text;

    const confirmBtn = document.getElementById('confirmationModalHref');
    confirmBtn.href = link;
    confirmBtn.className = 'btn ' + btnClass;
    confirmBtn.innerText = action;

    const modal = new bootstrap.Modal(document.getElementById('confirmationModal'));
    modal.show();
}
