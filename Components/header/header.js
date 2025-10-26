
document.addEventListener('DOMContentLoaded', () => {
    const hamburgerBtn = document.getElementById('hamburger-btn');
    const mobileMenu = document.getElementById('mobile-menu');
    const closeMenuBtn = document.getElementById('close-menu-btn');
    const menuBackdrop = document.getElementById('menu-backdrop');
    const header = document.querySelector('header');

    function openMenu() {
        mobileMenu.classList.remove('-translate-x-full');
        hamburgerBtn.classList.add('hidden');
        menuBackdrop.classList.add('active');
        header.classList.add('menu-open');
    }

    function closeMenu() {
        mobileMenu.classList.add('-translate-x-full');
        hamburgerBtn.classList.remove('hidden');
        menuBackdrop.classList.remove('active');
        header.classList.remove('menu-open');
    }

    hamburgerBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        if (mobileMenu.classList.contains('-translate-x-full')) {
            openMenu();
        } else {
            closeMenu();
        }
    });

    closeMenuBtn.addEventListener('click', closeMenu);
    menuBackdrop.addEventListener('click', closeMenu);

    document.querySelectorAll('#mobile-menu a').forEach(anchor => {
        anchor.addEventListener('click', closeMenu);
    });

    const menu = document.getElementById('mobile-menu');
    let isDragging = false;
    let touchStartX = 0;
    let menuStartX = 0;
    let touchStartTime = 0;

    document.addEventListener('touchstart', (e) => {
        const isMenuOpen = !menu.classList.contains('-translate-x-full');
        touchStartX = e.touches[0].clientX;

        if ((!isMenuOpen && touchStartX < 40) || (isMenuOpen && menu.contains(e.target))) {
            isDragging = true;
            touchStartTime = performance.now();
            menu.style.transition = 'none'; 
            const transformMatrix = new DOMMatrix(window.getComputedStyle(menu).transform);
            menuStartX = transformMatrix.m41;
        }
    }, { passive: true });

    document.addEventListener('touchmove', (e) => {
        if (!isDragging) return;

        const touchCurrentX = e.touches[0].clientX;
        const diffX = touchCurrentX - touchStartX;
        let newX = menuStartX + diffX;

        const menuWidth = menu.offsetWidth;
        if (newX > 0) {
            newX = newX * 0.3; 
        } else if (newX < -menuWidth) {
            const overscroll = newX + menuWidth;
            newX = -menuWidth + (overscroll * 0.3);
        }

        menu.style.transform = `translateX(${newX}px)`;
    }, { passive: true });

    document.addEventListener('touchend', (e) => {
        if (!isDragging) return;
        isDragging = false;

        menu.style.transition = 'transform 0.3s ease-out';

        const touchEndX = e.changedTouches[0].clientX;
        const deltaTime = performance.now() - touchStartTime;
        const velocityX = (touchEndX - touchStartX) / deltaTime;

        const transformMatrix = new DOMMatrix(window.getComputedStyle(menu).transform);
        const currentX = transformMatrix.m41;
        const menuWidth = menu.offsetWidth;

        if (velocityX > 0.4) {
            menu.style.transform = 'translateX(0px)';
            menu.classList.remove('-translate-x-full');
        } else if (velocityX < -0.4) {
            menu.style.transform = 'translateX(-100%)';
            menu.classList.add('-translate-x-full');
        } else {
            if (currentX > -menuWidth * 0.7) {
                menu.style.transform = 'translateX(0px)';
                menu.classList.remove('-translate-x-full');
            } else {
                menu.style.transform = 'translateX(-100%)';
                menu.classList.add('-translate-x-full');
            }
        }
        setTimeout(() => {
            menu.style.transition = '';
            menu.style.transform = '';
        }, 300);
    });
});
