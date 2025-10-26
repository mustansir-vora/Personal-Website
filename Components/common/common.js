
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.nav-link').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            document.querySelector(this.getAttribute('href')).scrollIntoView({
                behavior: 'smooth'
            });
        });
    });

    const sections = document.querySelectorAll('section');
    const navLinks = document.querySelectorAll('.nav-link');
    
    const removeActiveClasses = () => {
        navLinks.forEach(link => {
            link.classList.remove('bg-white/60', 'scale-105');
        });
    };

    const setActiveLink = (linkId) => {
        removeActiveClasses();
        const activeLinks = document.querySelectorAll(`.nav-link[href="#${linkId}"]`);
        activeLinks.forEach(activeLink => {
            if (activeLink) {
                if (window.innerWidth < 768) {
                    activeLink.classList.add('bg-white/60');
                } else {
                    activeLink.classList.add('bg-white/60', 'scale-105');
                }
            }
        });
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
        } else {
            removeActiveClasses();
        }
    };

    window.addEventListener('scroll', handleScroll);
    handleScroll();
});
