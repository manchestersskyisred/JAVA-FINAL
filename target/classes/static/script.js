// BBC fallback images for guaranteed display - updated with newer patterns
const BBC_IMAGES = [
    "https://ichef.bbci.co.uk/news/640/cpsprodpb/d094/live/57e6e0c0-1f87-11f0-a007-45ee4cc6069d.png.webp",
    "https://ichef.bbci.co.uk/news/640/cpsprodpb/36d4/live/df8e0960-83c5-11ee-91aa-99a7ae4b2349.jpg",
    "https://ichef.bbci.co.uk/news/640/cpsprodpb/1132/live/c09ca370-1e8b-11f0-a064-fb15f5897915.jpg"
];

document.addEventListener('DOMContentLoaded', function() {
    console.log("Document loaded, initializing functions");
    
    // Fix BBC images on page load
    fixBBCImages();
    
    // Add click handler for the "Fix BBC Images" button
    const fixBBCButton = document.getElementById("fix-bbc-images");
    if (fixBBCButton) {
        fixBBCButton.addEventListener("click", function() {
            fixBBCImages();
            alert("BBC images have been fixed!");
        });
    }
    
    // Setup image enlargement - USING ONLY ONE METHOD
    setupImageFullscreen();
    
    // Setup spelling check with fixed implementation
    setupSpellingCheck();
    
    // Setup article content expand/collapse with a delay to ensure DOM is fully loaded
    setupArticleContentToggle();
    // setTimeout(setupArticleContentToggle, 300);
    // setTimeout(setupArticleContentToggle, 1000);
    // setTimeout(setupArticleContentToggle, 2000);
    
    // Preload BBC images in the background
    preloadImages(BBC_IMAGES);
    
    fixBrokenImages();
    
    // Create observer to handle dynamically loaded images
    createImageObserver();
    
    // Watch for dynamically added article content
    createArticleContentObserver();
});

// Setup article content expand/collapse functionality
function setupArticleContentToggle() {
    console.log("Setting up article content toggle...");
    
    // Reinitialize all article-content containers regardless of initialization state
    const contentContainers = document.querySelectorAll('.article-content');
    
    if (contentContainers.length === 0) {
        console.log("No article content containers found");
        // Try again in a short while
        setTimeout(setupArticleContentToggle, 500);
        return;
    }
    
    console.log(`Found ${contentContainers.length} article content containers to initialize`);
    
    contentContainers.forEach((container, index) => {
        // Set initial state to collapsed if not already set
        if (!container.classList.contains('collapsed')) {
            container.classList.add('collapsed');
        }
        
        const showMoreBtn = container.querySelector('.show-more-btn');
        if (!showMoreBtn) {
            console.log(`No show more button found in container ${index}`);
            return;
        }
        
        // Make sure the button is visible
        showMoreBtn.style.display = 'inline-block';
        
        // Remove any existing event listeners
        const newBtn = showMoreBtn.cloneNode(true);
        showMoreBtn.parentNode.replaceChild(newBtn, showMoreBtn);
        
        // Add click event listener to the new button
        newBtn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            console.log(`Button clicked for container ${index}`);
            
            if (container.classList.contains('collapsed')) {
                // Expand content
                container.classList.remove('collapsed');
                newBtn.textContent = 'Show Less';
                console.log(`Expanded container ${index}`);
            } else {
                // Collapse content
                container.classList.add('collapsed');
                newBtn.textContent = 'Show More';
                console.log(`Collapsed container ${index}`);
                
                // Scroll back to the top of the article if needed
                const article = container.closest('.article');
                if (article) {
                    article.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }
            }
        });
        
        console.log(`Event listener added to button for container ${index}`);
    });
}

// Fixed spelling check function
function setupSpellingCheck() {
    const textInput = document.getElementById('text-input');
    const checkButton = document.getElementById('check-button');
    const resultArea = document.getElementById('result-area');
    const suggestionBox = document.getElementById('suggestion-box');
    const suggestionList = document.getElementById('suggestion-list');
    
    if (!checkButton || !textInput || !resultArea) {
        console.error("Spelling check elements not found");
        return;
    }
    
    console.log("Setting up spelling check");
    
    // Remove any existing event listeners (to prevent duplicates)
    const newCheckButton = checkButton.cloneNode(true);
    checkButton.parentNode.replaceChild(newCheckButton, checkButton);
    
    newCheckButton.addEventListener('click', function() {
        const text = textInput.value.trim();
        console.log("Checking spelling for:", text);
        
        if (text === '') {
            resultArea.textContent = 'Please enter text to check.';
            if (suggestionBox) suggestionBox.style.display = 'none';
            return;
        }
        
        resultArea.textContent = 'Checking spelling...';
        if (suggestionBox) suggestionBox.style.display = 'none';
        
        // Send request to the server with proper error handling
        fetch('/api/spellcheck', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `text=${encodeURIComponent(text)}`
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Server responded with ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log("Spelling check response:", data);
            
            if (!data.misspelledWords || data.misspelledWords.length === 0) {
                resultArea.textContent = 'No spelling errors found.';
                if (suggestionBox) suggestionBox.style.display = 'none';
            } else {
                resultArea.textContent = `Found ${data.misspelledWords.length} misspelled word(s):`;
                
                if (suggestionList) {
                    suggestionList.innerHTML = '';
                    data.misspelledWords.forEach(word => {
                        const wordElement = document.createElement('div');
                        wordElement.className = 'misspelled-word';
                        
                        const wordText = document.createElement('p');
                        wordText.innerHTML = `<strong>"${word.word}"</strong> - Suggestions:`;
                        wordElement.appendChild(wordText);
                        
                        if (word.suggestions && word.suggestions.length > 0) {
                            const suggestionsList = document.createElement('ul');
                            word.suggestions.forEach(suggestion => {
                                const item = document.createElement('li');
                                item.textContent = suggestion;
                                suggestionsList.appendChild(item);
                            });
                            wordElement.appendChild(suggestionsList);
                        } else {
                            const noSuggestions = document.createElement('p');
                            noSuggestions.textContent = "No suggestions available";
                            noSuggestions.style.fontStyle = "italic";
                            wordElement.appendChild(noSuggestions);
                        }
                        
                        suggestionList.appendChild(wordElement);
                    });
                    
                    if (suggestionBox) suggestionBox.style.display = 'block';
                }
            }
        })
        .catch(error => {
            console.error('Spelling check error:', error);
            resultArea.textContent = `Error checking spelling: ${error.message}. Please try again.`;
        });
    });
    
    // Allow pressing Enter to check spelling
    textInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            newCheckButton.click();
        }
    });
    
    console.log("Spelling check functionality initialized");
}

// Setup fullscreen functionality for images
function setupImageFullscreen() {
    console.log("Setting up fullscreen image functionality");
    
    // Create fullscreen overlay container
    const overlay = document.createElement('div');
    overlay.className = 'fullscreen-overlay';
    
    // Create the image element that will be shown in fullscreen
    const fullscreenImage = document.createElement('img');
    fullscreenImage.className = 'fullscreen-image';
    overlay.appendChild(fullscreenImage);
    
    // Create close button for the overlay
    const closeButton = document.createElement('button');
    closeButton.className = 'close-button';
    closeButton.textContent = '×';
    overlay.appendChild(closeButton);
    
    // Add overlay to document body
    document.body.appendChild(overlay);
    
    // Function to add click handlers to zoomable images
    function addZoomHandlers() {
        const zoomableImages = document.querySelectorAll('img.zoomable:not(.zoom-handler-added)');
        console.log(`Found ${zoomableImages.length} zoomable images without handlers`);
        
        zoomableImages.forEach(img => {
            img.classList.add('zoom-handler-added');
            img.style.cursor = 'pointer';
            
            img.addEventListener('click', function() {
                console.log("Image clicked, showing fullscreen");
                // Set the source of the fullscreen image to the clicked image
                fullscreenImage.src = this.src;
                // Show overlay
                overlay.classList.add('active');
                // Prevent scrolling on body
                document.body.style.overflow = 'hidden';
            });
        });
    }
    
    // Handle close button click
    closeButton.addEventListener('click', function() {
        overlay.classList.remove('active');
        // Re-enable scrolling
        document.body.style.overflow = '';
    });
    
    // Also close when clicking on the overlay background
    overlay.addEventListener('click', function(e) {
        if (e.target === overlay) {
            overlay.classList.remove('active');
            document.body.style.overflow = '';
        }
    });
    
    // Close on escape key press
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape' && overlay.classList.contains('active')) {
            overlay.classList.remove('active');
            document.body.style.overflow = '';
        }
    });
    
    // Add handlers to all existing zoomable images
    addZoomHandlers();
    
    // Create an observer to add handlers to new images
    const observer = new MutationObserver(mutations => {
        let hasNewImages = false;
        
        mutations.forEach(mutation => {
            if (mutation.type === 'childList' && mutation.addedNodes.length) {
                mutation.addedNodes.forEach(node => {
                    if (node.nodeType === 1) { // Element node
                        const images = node.querySelectorAll('img.zoomable:not(.zoom-handler-added)');
                        if (images.length) {
                            hasNewImages = true;
                        }
                    }
                });
            }
        });
        
        if (hasNewImages) {
            addZoomHandlers();
        }
    });
    
    // Start observing the document
    observer.observe(document.body, { childList: true, subtree: true });
}


function fixBrokenImages() {
    // Fix any broken images
    document.querySelectorAll('.article-image').forEach(img => {
        if (!img.complete || img.naturalWidth === 0) {
            img.src = "https://ichef.bbci.co.uk/news/976/cpsprodpb/2C79/production/_131435105_gettyimages-1443708542.jpg";
        }
        img.addEventListener('error', function() {
            this.src = "https://ichef.bbci.co.uk/news/976/cpsprodpb/2C79/production/_131435105_gettyimages-1443708542.jpg";
        });
    });
}

function createImageObserver() {
    // Watch for new images being added to the page
    const observer = new MutationObserver(mutations => {
        mutations.forEach(mutation => {
            mutation.addedNodes.forEach(node => {
                if (node.nodeType === 1) { // Element node
                    // Process any images within added nodes
                    const images = node.querySelectorAll('.article img:not(.image-container > img)');
                    images.forEach(img => {
                        // Add error handling
                        img.addEventListener('error', function() {
                            this.src = BBC_IMAGES[Math.floor(Math.random() * BBC_IMAGES.length)];
                        });
                        
                        // Create and use image container
                        if (!img.parentElement.classList.contains('image-container')) {
                            const container = document.createElement('div');
                            container.className = 'image-container';
                            img.parentNode.insertBefore(container, img);
                            container.appendChild(img);
                        }
                    });
                }
            });
        });
    });
    observer.observe(document.body, { childList: true, subtree: true });
}

function preloadImages(urls) {
    urls.forEach(function(url) {
        const img = new Image();
        img.src = url;
    });
}

function fixBBCImages() {
    console.log("Running fixBBCImages()");
    
    // Get all BBC article images
    const bbcImages = document.querySelectorAll(".bbc-article .article-image");
    
    bbcImages.forEach(function(img, index) {
        // Get a fallback image based on position (to ensure variety)
        const fallbackImg = BBC_IMAGES[index % BBC_IMAGES.length];
        
        // Force set the src to a working image
        img.src = fallbackImg;
        
        // Log success
        console.log(`BBC image ${index + 1} set to ${fallbackImg}`);
        
        // Remove any error handling to prevent loops
        img.onerror = null;
    });
}

// Observer for dynamically added article content
function createArticleContentObserver() {
    // Create an observer to watch for new article-content elements
    const observer = new MutationObserver(mutations => {
        let hasNewContent = false;
        
        mutations.forEach(mutation => {
            if (mutation.type === 'childList' && mutation.addedNodes.length) {
                mutation.addedNodes.forEach(node => {
                    if (node.nodeType === 1) { // Element node
                        const contentElements = node.querySelectorAll('.article-content');
                        if (contentElements.length) {
                            hasNewContent = true;
                            console.log(`Observer found ${contentElements.length} new article content containers`);
                            
                            // Call setup function to handle new content
                            setTimeout(setupArticleContentToggle, 100);
                        }
                    }
                });
            }
        });
    });
    
    // Start observing the document
    observer.observe(document.body, { childList: true, subtree: true });
    console.log("Article content observer started");
}
