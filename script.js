let currentInput = '';
let resultDisplay = document.getElementById('result');
let expressionDisplay = document.getElementById('expression');
let history = [];
let isNewCalculation = false;

// Factorial function
Math.factorial = function(n) {
    if (n < 0) return NaN;
    if (n === 0 || n === 1) return 1;
    let result = 1;
    for (let i = 2; i <= n; i++) {
        result *= i;
    }
    return result;
};

// Append value to input
function appendValue(value) {
    if (isNewCalculation) {
        currentInput = '';
        isNewCalculation = false;
        expressionDisplay.textContent = '';
    }

    // Handle factorial specially
    if (value === 'Math.factorial(') {
        // Get the number before the factorial
        const match = currentInput.match(/[\d.]+$/);
        if (match) {
            const num = parseFloat(match[0]);
            if (!isNaN(num) && Number.isInteger(num) && num >= 0) {
                currentInput = currentInput.slice(0, -match[0].length);
                currentInput += Math.factorial(num);
                updateDisplay();
                return;
            }
        }
        currentInput += value;
        updateDisplay();
        return;
    }

    currentInput += value;
    updateDisplay();
}

// Update display
function updateDisplay() {
    resultDisplay.textContent = currentInput || '0';
}

// Clear display
function clearDisplay() {
    currentInput = '';
    expressionDisplay.textContent = '';
    resultDisplay.textContent = '0';
    isNewCalculation = false;
}

// Delete last character
function deleteLast() {
    if (isNewCalculation) {
        clearDisplay();
        return;
    }
    currentInput = currentInput.slice(0, -1);
    updateDisplay();
}

// Calculate result
function calculate() {
    try {
        let expression = currentInput;

        // Replace display symbols with JavaScript equivalents
        let sanitized = expression
            .replace(/÷/g, '/')
            .replace(/×/g, '*')
            .replace(/−/g, '-')
            .replace(/Math.ln\(/g, 'Math.log(')
            .replace(/√\(/g, 'Math.sqrt(')
            .replace(/x\^y/g, '**');

        // Handle factorial if present
        if (sanitized.includes('!')) {
            // Custom factorial handling
            const factMatch = sanitized.match(/(\d+)!/);
            if (factMatch) {
                const num = parseInt(factMatch[1]);
                if (!isNaN(num) && num >= 0) {
                    const factResult = Math.factorial(num);
                    sanitized = sanitized.replace(`${num}!`, factResult);
                }
            }
        }

        // Use Function constructor for safe evaluation with Math functions
        const result = new Function(`return (${sanitized})`)();

        if (isNaN(result) || !isFinite(result)) {
            throw new Error('Invalid calculation');
        }

        // Format result
        const formattedResult = Number.isInteger(result) ? result : parseFloat(result.toFixed(10));

        // Add to history
        addToHistory(currentInput, formattedResult);

        // Display result
        expressionDisplay.textContent = currentInput + ' =';
        currentInput = formattedResult.toString();
        resultDisplay.textContent = currentInput;
        isNewCalculation = true;

    } catch (error) {
        resultDisplay.textContent = 'Error';
        setTimeout(() => {
            clearDisplay();
        }, 1500);
    }
}

// Add to history
function addToHistory(expression, result) {
    history.push({ expression, result });
    if (history.length > 50) history.shift(); // Limit history to 50 items
    updateHistoryDisplay();
}

// Update history display
function updateHistoryDisplay() {
    const historyList = document.getElementById('historyList');
    historyList.innerHTML = history.map(item => `
        <div class="history-item">
            <span class="h-expr">${item.expression}</span>
            <span class="h-result">= ${item.result}</span>
        </div>
    `).join('');
}

// Toggle history panel
function toggleHistory() {
    const panel = document.getElementById('historyPanel');
    panel.classList.toggle('active');
}

// Clear history
function clearHistory() {
    history = [];
    updateHistoryDisplay();
}

// Keyboard support
document.addEventListener('keydown', function(event) {
    const key = event.key;

    if (key >= '0' && key <= '9') {
        appendValue(key);
    } else if (key === '.') {
        appendValue('.');
    } else if (key === '+' || key === '-' || key === '*' || key === '/') {
        appendValue(key);
    } else if (key === 'Enter' || key === '=') {
        event.preventDefault();
        calculate();
    } else if (key === 'Backspace') {
        deleteLast();
    } else if (key === 'Escape' || key === 'c' || key === 'C') {
        clearDisplay();
    } else if (key === '(' || key === ')') {
        appendValue(key);
    } else if (key === '%') {
        appendValue('%');
    }
});

// Prevent default keyboard shortcuts
document.addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
    }
});

// Initialize
clearDisplay();