function typeWriter(element, text, speed = 75, callback) {
    let i = 0;
    element.innerHTML = '';
    element.style.opacity = '1';
    element.style.transform = 'translateY(0)';
    
    function type() {
        if (i < text.length) {
            element.innerHTML += text.charAt(i);
            i++;
            setTimeout(type, speed);
        } else {
            const cursor = document.createElement('span');
            cursor.className = 'terminal-cursor';
            element.appendChild(cursor);
            if (callback && typeof callback === 'function') {
                callback();
            }
        }
    }
    type();
}

setTimeout(() => {
    const nameElement = document.getElementById('typing-name');
    const subtitleElement = document.getElementById('typing-subtitle');
    
    if (nameElement) {
        typeWriter(nameElement, "Hello, I'm Mustansir Vora", 75, () => {
            if (subtitleElement) {
                typeWriter(subtitleElement, "Full Stack Developer & AI Research Enthusiast", 50, () => {
                    const mainNav = document.getElementById('main-nav');
                    const connectBtn = document.getElementById('connect-btn');
                    const resumeBtn = document.getElementById('download-resume-btn');
                    const socialIcons = document.querySelectorAll('.social-icons a');

                    if (mainNav) mainNav.classList.add('animate-slide-in-scale-top');
                    if (connectBtn) connectBtn.classList.add('animate-slide-in-scale-left');
                    if (resumeBtn) resumeBtn.classList.add('animate-slide-in-scale-right');
                    
                    socialIcons.forEach((icon, index) => {
                        icon.classList.add('animate-slide-in-scale-bottom');
                        icon.style.animationDelay = `${index * 0.15 + 0.2}s`;
                    });
                });
            }
        });
    }
}, 300); 

document.addEventListener('DOMContentLoaded', () => {
    const sparkSize = 10;
    const sparkRadius = 15;
    const sparkCount = 8;
    const duration = 400;
    const easing = 'ease-out';
    const extraScale = 1.0;

    const canvas = document.createElement('canvas');
    document.body.appendChild(canvas);
    canvas.style.position = 'fixed';
    canvas.style.top = '0';
    canvas.style.left = '0';
    canvas.style.width = '100%';
    canvas.style.height = '100%';
    canvas.style.pointerEvents = 'none';
    canvas.style.zIndex = '9999';

    const ctx = canvas.getContext('2d');
    let sparks = [];

    const resizeCanvas = () => {
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
    };

    const easeFunc = (t) => {
        switch (easing) {
            case 'linear':
                return t;
            case 'ease-in':
                return t * t;
            case 'ease-in-out':
                return t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;
            default:
                return t * (2 - t);
        }
    };

    const draw = (timestamp) => {
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        sparks = sparks.filter(spark => {
            const elapsed = timestamp - spark.startTime;
            if (elapsed >= duration) {
                return false;
            }

            const progress = elapsed / duration;
            const eased = easeFunc(progress);

            const distance = eased * sparkRadius * extraScale;
            const lineLength = sparkSize * (1 - eased);

            const x1 = spark.x + distance * Math.cos(spark.angle);
            const y1 = spark.y + distance * Math.sin(spark.angle);
            const x2 = spark.x + (distance + lineLength) * Math.cos(spark.angle);
            const y2 = spark.y + (distance + lineLength) * Math.sin(spark.angle);

            ctx.strokeStyle = spark.color;
            ctx.lineWidth = 2;
            ctx.beginPath();
            ctx.moveTo(x1, y1);
            ctx.lineTo(x2, y2);
            ctx.stroke();

            return true;
        });

        requestAnimationFrame(draw);
    };

    const handleClick = (e) => {
        const x = e.clientX;
        const y = e.clientY;
        const sparkColor = getComputedStyle(document.body).color;

        const now = performance.now();
        const newSparks = Array.from({ length: sparkCount }, (_, i) => ({
            x,
            y,
            angle: (2 * Math.PI * i) / sparkCount,
            startTime: now,
            color: sparkColor
        }));

        sparks.push(...newSparks);
    };

    window.addEventListener('resize', resizeCanvas);
    document.addEventListener('click', handleClick);

    resizeCanvas();
    requestAnimationFrame(draw);
});

document.addEventListener('DOMContentLoaded', () => {
    const cards = document.querySelectorAll('.card');

    const DEFAULT_PARTICLE_COUNT = 15;
    const DEFAULT_SPOTLIGHT_RADIUS = 400;
    const MOBILE_BREAKPOINT = 768;

    const isMobile = window.innerWidth <= MOBILE_BREAKPOINT;

    const getContrastingColor = (color) => {
        const rgb = color.match(/\d+/g);
        if (!rgb) return '255, 255, 255';
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
            const section = card.closest('section');
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
            const section = card.closest('section');
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
        const section = e.target.closest('section');
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