
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.nav-link').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            document.querySelector(this.getAttribute('href')).scrollIntoView({
                behavior: 'smooth'
            });
        });
    });

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

    // Modal functionality
    const projectModal = document.getElementById('project-modal');
    const closeModalBtn = document.getElementById('close-modal');
    const modalProjectTitle = document.getElementById('modal-project-title');
    const modalProjectDescription = document.getElementById('modal-project-description');
    const modalProjectDetailedDescription = document.getElementById('modal-project-detailed-description');

    const personalProjectModal = document.getElementById('personal-project-modal');
    const closePersonalModalBtn = document.getElementById('close-personal-modal');
    const personalModalProjectTitle = document.getElementById('personal-modal-project-title');
    const personalModalProjectDescription = document.getElementById('personal-modal-project-description');
    const personalModalProjectDetailedDescription = document.getElementById('personal-modal-project-detailed-description');

    const projectCards = document.querySelectorAll('.project-card');

    const openModal = (card) => {
        const title = card.dataset.projectTitle;
        const description = card.dataset.projectDescription;
        const detailedDescription = card.dataset.projectDetailedDescription;
        const isPersonal = card.closest('.grid').previousElementSibling.textContent.includes('Personal Projects');

        if (isPersonal) {
            personalModalProjectTitle.textContent = title;
            personalModalProjectDescription.innerHTML = description;
            personalModalProjectDetailedDescription.innerHTML = detailedDescription;
            personalProjectModal.classList.remove('hidden');
            closePersonalModalBtn.focus();
        } else {
            modalProjectTitle.textContent = title;
            modalProjectDescription.textContent = description;
            modalProjectDetailedDescription.innerHTML = detailedDescription;
            projectModal.classList.remove('hidden');
            closeModalBtn.focus();
        }
    };

    projectCards.forEach(card => {
        card.addEventListener('click', () => openModal(card));
        card.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                openModal(card);
            }
        });
    });
    
    closeModalBtn.addEventListener('click', () => {
        projectModal.classList.add('hidden');
    });

    closePersonalModalBtn.addEventListener('click', () => {
        personalProjectModal.classList.add('hidden');
    });

    projectModal.addEventListener('click', (e) => {
        if (e.target === projectModal) {
            projectModal.classList.add('hidden');
        }
    });

    personalProjectModal.addEventListener('click', (e) => {
        if (e.target === personalProjectModal) {
            personalProjectModal.classList.add('hidden');
        }
    });

    const timelineAirplane = document.getElementById('timeline-airplane');
    const timelineItems = document.querySelectorAll('.timeline-item');
    function updateTimelineAirplanePosition() {
        let activeItem = null;
        for (const item of timelineItems) {
            if (item.getBoundingClientRect().top < window.innerHeight / 2) {
                activeItem = item;
            }
        }
        if (activeItem) {
            const topPosition = activeItem.offsetTop + 16;
            timelineAirplane.style.top = `${topPosition}px`;
            let rotation = 90;
            if (window.innerWidth >= 768 && activeItem.offsetLeft > 0) {
                rotation = -90;
            }
            timelineAirplane.style.transform = `translateX(-50%) rotate(${rotation}deg)`;
        }
    }
    
    function throttle(func, limit) {
        let inThrottle;
        return function() {
            const args = arguments;
            const context = this;
            if (!inThrottle) {
                func.apply(context, args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        }
    }

    const scrollHandler = () => {
        updateTimelineAirplanePosition();
    };

    window.addEventListener('scroll', throttle(scrollHandler, 10));
    updateTimelineAirplanePosition();

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

    projectCards.forEach(card => {
        const p = document.createElement('p');
        p.className = 'text-xs opacity-50 mt-4 text-center';
        p.textContent = 'Tap to expand';
        card.appendChild(p);
    });

    const sections = document.querySelectorAll('section');
    const navLinks = document.querySelectorAll('.md\\:flex a');
    
    const removeActiveClasses = () => {
        navLinks.forEach(link => {
            link.classList.remove('bg-white/60', 'scale-105');
        });
    };

    const setActiveLink = (linkId) => {
        removeActiveClasses();
        const activeLink = document.querySelector(`.md\\:flex a[href="#${linkId}"]`);
        if (activeLink) {
            activeLink.classList.add('bg-white/60', 'scale-105');
        }
    };
    
    const handleScroll = () => {
        let currentSectionId = '';
        const scrollY = window.scrollY;

        if (scrollY < 100) {
            setActiveLink('home');
            return;
        }
        
        sections.forEach(section => {
            const sectionTop = section.offsetTop - 150; 
            const sectionHeight = section.offsetHeight;

            if (scrollY >= sectionTop && scrollY < sectionTop + sectionHeight) {
                currentSectionId = section.id;
            }
        });

        if (currentSectionId === 'tech-skills' || currentSectionId === 'key-skills') {
            setActiveLink('tech-skills'); 
        } else if (currentSectionId === 'portfolio') {
            setActiveLink('portfolio');
        } else if (currentSectionId) {
            setActiveLink(currentSectionId);
        }
    };

    window.addEventListener('scroll', handleScroll);
    handleScroll();
});
