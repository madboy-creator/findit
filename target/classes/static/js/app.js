/**
 * FINDIT - Main Application JavaScript
 * Version: 2.0.0
 * Dependencies: Alpine.js (already loaded)
 */

// Global utility functions
window.FindIt = {
    // Format date for display
    formatDate(date, format = 'short') {
        const d = new Date(date);
        if (format === 'short') {
            return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
        } else if (format === 'long') {
            return d.toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
        }
        return d.toLocaleDateString();
    },
    
    // Truncate text
    truncate(text, length = 100) {
        if (!text) return '';
        return text.length > length ? text.substring(0, length) + '...' : text;
    },
    
    // Debounce function for search inputs
    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },
    
    // Show confirmation dialog
    confirm(message, callback) {
        if (confirm(message)) {
            callback();
        }
    },
    
    // Copy to clipboard
    copyToClipboard(text) {
        navigator.clipboard.writeText(text).then(() => {
            window.dispatchEvent(new CustomEvent('toast', {
                detail: { message: 'Copied to clipboard!', type: 'success', id: Date.now() }
            }));
        }).catch(() => {
            window.dispatchEvent(new CustomEvent('toast', {
                detail: { message: 'Failed to copy', type: 'error', id: Date.now() }
            }));
        });
    },
    
    // Download data as JSON (for reports)
    downloadJSON(data, filename = 'report.json') {
        const json = JSON.stringify(data, null, 2);
        const blob = new Blob([json], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.click();
        URL.revokeObjectURL(url);
    }
};

// Auto-initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    // Add loading class to body
    document.body.classList.add('loaded');
    
    // Auto-dismiss flash messages after 5 seconds
    document.querySelectorAll('.alert-success, .alert-error, .alert-info, .alert-warning').forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    });
    
    // Add smooth scrolling to anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            const href = this.getAttribute('href');
            if (href !== '#' && href !== '') {
                const target = document.querySelector(href);
                if (target) {
                    e.preventDefault();
                    target.scrollIntoView({ behavior: 'smooth' });
                }
            }
        });
    });
});

// Handle before unload for unsaved changes
let formChanged = false;
document.addEventListener('DOMContentLoaded', () => {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('input', () => {
            formChanged = true;
        });
        form.addEventListener('submit', () => {
            formChanged = false;
        });
    });
    
    window.addEventListener('beforeunload', (e) => {
        if (formChanged) {
            e.preventDefault();
            e.returnValue = 'You have unsaved changes. Are you sure you want to leave?';
        }
    });
});

// Export for module usage (if needed)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = FindIt;
}