
document.addEventListener('DOMContentLoaded', () => {
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

    projectCards.forEach(card => {
        const p = document.createElement('p');
        p.className = 'text-xs opacity-50 mt-4 text-center';
        p.textContent = 'Tap to expand';
        card.appendChild(p);
    });
});
