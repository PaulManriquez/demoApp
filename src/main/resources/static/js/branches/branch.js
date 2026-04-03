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

// ================= PRODUCT PAGE MODAL =================
function showProductModal(name, items, wholesalePrice, retailPrice, special, description, id) {

    document.getElementById('productModalName').value = name;
    document.getElementById('productModalItems').value = items;
    document.getElementById('productModalWholesalePrice').value = wholesalePrice;
    document.getElementById('productModalRetailPrice').value = retailPrice;
    document.getElementById('productModalDescription').value = description;
    document.getElementById('productIdModal').value = id;

    const modal = new bootstrap.Modal(document.getElementById('editProductModal'));
    modal.show();
}
