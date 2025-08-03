# Personal Portfolio Website

This repository contains the source code for my personal portfolio website, designed to showcase my skills, experience, and projects in a visually engaging and interactive manner.

## About This Project

This is a single-page, fully responsive portfolio built with modern web technologies. The standout feature is a dynamic day-to-night transition that simulates the passage of time as you scroll through the page. This, combined with a clean and modern design, creates a unique and memorable user experience.

## Key Features

*   **Dynamic Day/Night Cycle:** The website's color scheme transitions smoothly from sunrise to day, sunset, and night as the user scrolls. This is achieved with performant CSS animations for a seamless experience.
*   **Interactive Sun/Moon Animation:** A celestial object, representing the sun during the day and the moon at night, moves across the sky in sync with the scroll-driven time of day.
*   **Responsive Design:** The layout is optimized for all screen sizes, from mobile phones to widescreen desktops, ensuring a consistent and accessible experience for all users.
*   **Interactive Project Showcase:** Both professional and personal projects are displayed in a clean, card-based layout. Clicking on a project opens a modal with a detailed description and links to live demos or source code where available.
*   **Skills and Experience Timeline:** My technical skills are presented in an organized grid, and my work experience is displayed in a vertical timeline with an animated airplane icon that follows the user's scroll position.
*   **Smooth Scrolling & Navigation:** The website features a sticky navigation bar with smooth scrolling to all sections, making it easy to navigate the single-page layout.

## Technologies Used

*   **HTML5:** For the semantic structure and content of the website.
*   **CSS3:** For all styling, including the responsive design and the scroll-driven animations. The day/night cycle and sun/moon animations are implemented using modern CSS features like `@property` and `animation-timeline` for optimal performance.
*   **Tailwind CSS:** A utility-first CSS framework used for rapid and consistent styling.
*   **JavaScript:** Used for DOM manipulation, such as dynamically populating the skills and projects sections, and for handling interactive elements like the project modals and the timeline airplane animation.

## How to Use

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/mustansir-vora/Personal-Website.git
    ```
2.  **Navigate to the project directory:**
    ```bash
    cd Personal-Website
    ```
3.  **Open `index.html` in your web browser** to view the website.

## Customization

All the website's content is located in the `index.html` file. You can easily customize the text, project details, and skills by editing the following sections:

*   **About Me:** Update the text in the `"about"` section.
*   **Tech Stack & Soft Skills:** Modify the `techSkills` and `keySkills` arrays in the `<script>` section to update the skills displayed.
*   **Work Experience:** Edit the `"work-experience"` section to reflect your career history.
*   **Projects:** Update the project cards in the `"portfolio"` section. The `data-` attributes in each project card are used to populate the modal windows.