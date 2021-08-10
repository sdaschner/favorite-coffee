function init() {
    document.querySelectorAll("a.rating").forEach(el => {
        el.addEventListener('mouseenter', onMouseEnter);
        el.addEventListener('mouseleave', onMouseLeave);
        el.addEventListener('click', onClick);
    });
}

function onMouseEnter(ev) {
    const rating = ev.target.closest('a').getAttribute('data-rating');
    if (rating) {
        let parentNode = ev.target.parentNode;
        for (let i = 1; i <= parseInt(rating); i++) {
            parentNode.querySelector(`a[data-rating='${i}']`).classList.add('hover');
        }
    }
}

function onMouseLeave(ev) {
    document.querySelectorAll('a.rating.hover').forEach(el => el.classList.remove('hover'));
}

function onClick(ev) {
    ev.preventDefault();
    const uuid = ev.target.closest('tr').getAttribute('data-bean');
    const rating = ev.target.closest('a').getAttribute('data-rating');
    console.log(uuid, rating, ev.target);
    if (uuid && rating)
        fetch(`${window.location.origin}/beans/${uuid}/ratings`, {
            method: "PUT",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({rating: parseInt(rating)})
        })
            .then(() => window.location.reload());
}

init();
