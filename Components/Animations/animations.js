
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
