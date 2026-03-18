function showEditBranchModal(name, address, mapsLink, id, userId) {

    document.getElementById('branchModalName').value = name;
    document.getElementById('branchModalAddress').value = address;
    document.getElementById('branchModalMapsLink').value = mapsLink;
    document.getElementById('branchIdModal').value = id;

    // Bootstrap 5 way                            This html front end is being called
    const modalElement = document.getElementById('editBranchModal');
    const modal = new bootstrap.Modal(modalElement);
    modal.show();
}
