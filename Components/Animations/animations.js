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
    const sparkColor = '#fff';
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

            ctx.strokeStyle = sparkColor;
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

        const now = performance.now();
        const newSparks = Array.from({ length: sparkCount }, (_, i) => ({
            x,
            y,
            angle: (2 * Math.PI * i) / sparkCount,
            startTime: now
        }));

        sparks.push(...newSparks);
    };

    window.addEventListener('resize', resizeCanvas);
    document.addEventListener('click', handleClick);

    resizeCanvas();
    requestAnimationFrame(draw);
});