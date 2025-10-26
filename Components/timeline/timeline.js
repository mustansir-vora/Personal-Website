
document.addEventListener('DOMContentLoaded', () => {
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
});
