
document.addEventListener('DOMContentLoaded', () => {
    const cards = document.querySelectorAll('.card');

    const DEFAULT_PARTICLE_COUNT = 15;
    const DEFAULT_SPOTLIGHT_RADIUS = 400;
    const MOBILE_BREAKPOINT = 768;

    const isMobile = window.innerWidth <= MOBILE_BREAKPOINT;

    const getContrastingColor = (color) => {
        const rgb = color.match(/\d+/g);
        if (!rgb) return '#000';
        const brightness = (parseInt(rgb[0]) * 299 + parseInt(rgb[1]) * 587 + parseInt(rgb[2]) * 114) / 1000;
        return brightness > 125 ? '0, 0, 0' : '255, 255, 255';
    };

    const createParticleElement = (x, y, color) => {
        const el = document.createElement('div');
        el.className = 'particle';
        el.style.cssText = `
            position: absolute;
            width: 4px;
            height: 4px;
            border-radius: 50%;
            background: rgba(${color}, 1);
            box-shadow: 0 0 6px rgba(${color}, 0.6);
            pointer-events: none;
            z-index: 100;
            left: ${x}px;
            top: ${y}px;
        `;
        return el;
    };

    const calculateSpotlightValues = radius => ({
        proximity: radius * 0.5,
        fadeDistance: radius * 0.75
    });

    const updateCardGlowProperties = (card, mouseX, mouseY, glow, radius) => {
        const rect = card.getBoundingClientRect();
        const relativeX = ((mouseX - rect.left) / rect.width) * 100;
        const relativeY = ((mouseY - rect.top) / rect.height) * 100;

        card.style.setProperty('--glow-x', `${relativeX}%`);
        card.style.setProperty('--glow-y', `${relativeY}%`);
        card.style.setProperty('--glow-intensity', glow.toString());
        card.style.setProperty('--glow-radius', `${radius}px`);
    };

    cards.forEach(card => {
        const particleCount = DEFAULT_PARTICLE_COUNT;
        const enableTilt = true;
        const clickEffect = true;
        const enableMagnetism = true;

        let timeouts = [];
        let particles = [];
        let isHovered = false;
        let magnetismAnimation;

        const initializeParticles = (glowColor) => {
            const { width, height } = card.getBoundingClientRect();
            return Array.from({ length: particleCount }, () =>
                createParticleElement(Math.random() * width, Math.random() * height, glowColor)
            );
        };

        let memoizedParticles = [];

        const clearAllParticles = () => {
            timeouts.forEach(clearTimeout);
            timeouts = [];
            if (magnetismAnimation) magnetismAnimation.kill();

            particles.forEach(particle => {
                gsap.to(particle, {
                    scale: 0,
                    opacity: 0,
                    duration: 0.3,
                    ease: 'back.in(1.7)',
                    onComplete: () => {
                        particle.parentNode?.removeChild(particle);
                    }
                });
            });
            particles = [];
        };

        const animateParticles = (glowColor) => {
            if (!isHovered) return;

            memoizedParticles = initializeParticles(glowColor);

            memoizedParticles.forEach((particle, index) => {
                const timeoutId = setTimeout(() => {
                    if (!isHovered) return;

                    const clone = particle.cloneNode(true);
                    card.appendChild(clone);
                    particles.push(clone);

                    gsap.fromTo(clone, { scale: 0, opacity: 0 }, { scale: 1, opacity: 1, duration: 0.3, ease: 'back.out(1.7)' });

                    gsap.to(clone, {
                        x: (Math.random() - 0.5) * 100,
                        y: (Math.random() - 0.5) * 100,
                        rotation: Math.random() * 360,
                        duration: 2 + Math.random() * 2,
                        ease: 'none',
                        repeat: -1,
                        yoyo: true
                    });

                    gsap.to(clone, {
                        opacity: 0.3,
                        duration: 1.5,
                        ease: 'power2.inOut',
                        repeat: -1,
                        yoyo: true
                    });
                }, index * 100);

                timeouts.push(timeoutId);
            });
        };

        card.addEventListener('mouseenter', () => {
            isHovered = true;
            const section = card.closest('.bento-section');
            const bgColor = section ? getComputedStyle(section).backgroundColor : getComputedStyle(document.body).backgroundColor;
            const glowColor = getContrastingColor(bgColor);
            card.style.setProperty('--glow-color', glowColor);
            animateParticles(glowColor);

            if (enableTilt) {
                gsap.to(card, {
                    rotateX: 5,
                    rotateY: 5,
                    duration: 0.3,
                    ease: 'power2.out',
                    transformPerspective: 1000
                });
            }
        });

        card.addEventListener('mouseleave', () => {
            isHovered = false;
            clearAllParticles();

            if (enableTilt) {
                gsap.to(card, {
                    rotateX: 0,
                    rotateY: 0,
                    duration: 0.3,
                    ease: 'power2.out'
                });
            }

            if (enableMagnetism) {
                gsap.to(card, {
                    x: 0,
                    y: 0,
                    duration: 0.3,
                    ease: 'power2.out'
                });
            }
        });

        card.addEventListener('mousemove', e => {
            if (!enableTilt && !enableMagnetism) return;

            const rect = card.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;
            const centerX = rect.width / 2;
            const centerY = rect.height / 2;

            if (enableTilt) {
                const rotateX = ((y - centerY) / centerY) * -10;
                const rotateY = ((x - centerX) / centerX) * 10;

                gsap.to(card, {
                    rotateX,
                    rotateY,
                    duration: 0.1,
                    ease: 'power2.out',
                    transformPerspective: 1000
                });
            }

            if (enableMagnetism) {
                const magnetX = (x - centerX) * 0.05;
                const magnetY = (y - centerY) * 0.05;

                magnetismAnimation = gsap.to(card, {
                    x: magnetX,
                    y: magnetY,
                    duration: 0.3,
                    ease: 'power2.out'
                });
            }
        });

        card.addEventListener('click', e => {
            if (!clickEffect) return;

            const rect = card.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;
            const section = card.closest('.bento-section');
            const bgColor = section ? getComputedStyle(section).backgroundColor : getComputedStyle(document.body).backgroundColor;
            const glowColor = getContrastingColor(bgColor);

            const maxDistance = Math.max(
                Math.hypot(x, y),
                Math.hypot(x - rect.width, y),
                Math.hypot(x, y - rect.height),
                Math.hypot(x - rect.width, y - rect.height)
            );

            const ripple = document.createElement('div');
            ripple.style.cssText = `
                position: absolute;
                width: ${maxDistance * 2}px;
                height: ${maxDistance * 2}px;
                border-radius: 50%;
                background: radial-gradient(circle, rgba(${glowColor}, 0.4) 0%, rgba(${glowColor}, 0.2) 30%, transparent 70%);
                left: ${x - maxDistance}px;
                top: ${y - maxDistance}px;
                pointer-events: none;
                z-index: 1000;
            `;

            card.appendChild(ripple);

            gsap.fromTo(
                ripple,
                {
                    scale: 0,
                    opacity: 1
                },
                {
                    scale: 1,
                    opacity: 0,
                    duration: 0.8,
                    ease: 'power2.out',
                    onComplete: () => ripple.remove()
                }
            );
        });
    });

    const spotlight = document.createElement('div');
    spotlight.className = 'global-spotlight';
    document.body.appendChild(spotlight);

    document.addEventListener('mousemove', e => {
        const section = e.target.closest('.bento-section');
        const bgColor = section ? getComputedStyle(section).backgroundColor : getComputedStyle(document.body).backgroundColor;
        const glowColor = getContrastingColor(bgColor);
        spotlight.style.background = `radial-gradient(circle,
            rgba(${glowColor}, 0.15) 0%,
            rgba(${glowColor}, 0.08) 15%,
            rgba(${glowColor}, 0.04) 25%,
            rgba(${glowColor}, 0.02) 40%,
            rgba(${glowColor}, 0.01) 65%,
            transparent 70%
        )`

        const { proximity, fadeDistance } = calculateSpotlightValues(DEFAULT_SPOTLIGHT_RADIUS);
        let minDistance = Infinity;

        cards.forEach(card => {
            const cardRect = card.getBoundingClientRect();
            const centerX = cardRect.left + cardRect.width / 2;
            const centerY = cardRect.top + cardRect.height / 2;
            const distance =
                Math.hypot(e.clientX - centerX, e.clientY - centerY) - Math.max(cardRect.width, cardRect.height) / 2;
            const effectiveDistance = Math.max(0, distance);

            minDistance = Math.min(minDistance, effectiveDistance);

            let glowIntensity = 0;
            if (effectiveDistance <= proximity) {
                glowIntensity = 1;
            } else if (effectiveDistance <= fadeDistance) {
                glowIntensity = (fadeDistance - effectiveDistance) / (fadeDistance - proximity);
            }

            updateCardGlowProperties(card, e.clientX, e.clientY, glowIntensity, DEFAULT_SPOTLIGHT_RADIUS);
        });

        gsap.to(spotlight, {
            left: e.clientX,
            top: e.clientY,
            duration: 0.1,
            ease: 'power2.out'
        });

        const targetOpacity =
            minDistance <= proximity
                ? 0.8
                : minDistance <= fadeDistance
                    ? ((fadeDistance - minDistance) / (fadeDistance - proximity)) * 0.8
                    : 0;

        gsap.to(spotlight, {
            opacity: targetOpacity,
            duration: targetOpacity > 0 ? 0.2 : 0.5,
            ease: 'power2.out'
        });
    });

    document.addEventListener('mouseleave', () => {
        cards.forEach(card => {
            card.style.setProperty('--glow-intensity', '0');
        });
        gsap.to(spotlight, {
            opacity: 0,
            duration: 0.3,
            ease: 'power2.out'
        });
    });
});
