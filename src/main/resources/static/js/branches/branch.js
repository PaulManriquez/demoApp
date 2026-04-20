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

// ================= PURCHASE EDIT MODAL =================
function showPurchaseModal(date, id, providerId, userId) {

    console.log("Opening purchase modal:", { date, id, providerId, userId });

    // In coming date
    if (date) {
        // If coming as ISO or full string thus ensure YYYY-MM-DD
        const formattedDate = date.substring(0, 10);
        document.querySelector('#editPurchaseModal input[type="date"]').value = formattedDate;
    }

    // Set purchase ID
    document.getElementById('purchaseModalId').value = id;

    // Set provider
    document.querySelector('#editPurchaseModal select').value = providerId;

    // Set user
    document.getElementById('purchaseModalUserId').value = userId;

    // Open modal (Bootstrap 5)
    const modalElement = document.getElementById('editPurchaseModal');
    const modal = new bootstrap.Modal(modalElement);
    modal.show();
}

// Administration/sales/products
// Dynamic price display
//document.addEventListener("DOMContentLoaded", function () {
//
//    const productSelect = document.getElementById("productSelect");
//    const priceInput = document.getElementById("priceInput");
//
//    function updatePrice() {
//        const selectedOption = productSelect.options[productSelect.selectedIndex];
//        const price = selectedOption.getAttribute("data-price");
//
//        if (price) {
//            priceInput.value = price;
//        }
//    }
//
//    // Initial load
//    updatePrice();
//
//    // On change
//    productSelect.addEventListener("change", updatePrice);
//});

document.addEventListener("DOMContentLoaded", function () {

    const productSelect = document.getElementById("productSelect");
    const priceInput = document.getElementById("priceInput");
    const stockLabel = document.getElementById("stockAvailable");
    const quantityInput = document.getElementById("quantityInput");

    function updateUI() {

        const selected = productSelect.options[productSelect.selectedIndex];

        const price = selected.getAttribute("data-price");
        const stock = selected.getAttribute("data-stock") || 0;

        // Update price
        if (price) {
            priceInput.value = parseFloat(price).toFixed(2);
        }

        // Update stock label
        stockLabel.innerText = stock;

        // Limit quantity
        quantityInput.max = stock;

        // Reset quantity if invalid
        if (quantityInput.value > stock) {
            quantityInput.value = stock;
        }
    }

    updateUI();
    productSelect.addEventListener("change", updateUI);
});