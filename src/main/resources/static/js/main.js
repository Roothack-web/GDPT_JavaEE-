// 主JavaScript文件

// 全局配置
const API_BASE = '/api';

// 显示消息
function showMessage(type, message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    const container = document.getElementById('messageContainer') || document.body;
    container.insertBefore(alertDiv, container.firstChild);
    
    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}

// 格式化日期
function formatDate(date) {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleString('zh-CN');
}

// 格式化价格
function formatPrice(price) {
    if (!price) return '¥0.00';
    return `¥${parseFloat(price).toFixed(2)}`;
}

// 加载数据
async function loadData(url, params = {}) {
    try {
        const queryString = new URLSearchParams(params).toString();
        const fullUrl = queryString ? `${url}?${queryString}` : url;
        
        const response = await fetch(fullUrl);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('加载数据失败:', error);
        showMessage('danger', '加载数据失败: ' + error.message);
        throw error;
    }
}

// 提交表单数据
async function submitData(url, method, data) {
    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('提交数据失败:', error);
        showMessage('danger', '提交数据失败: ' + error.message);
        throw error;
    }
}

// 确认对话框
function confirmAction(message) {
    return new Promise((resolve) => {
        if (window.confirm(message)) {
            resolve(true);
        } else {
            resolve(false);
        }
    });
}

// 初始化页面
document.addEventListener('DOMContentLoaded', function() {
    // 自动隐藏警告消息
    setTimeout(() => {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            const bsAlert = new bootstrap.Alert(alert);
            setTimeout(() => bsAlert.close(), 5000);
        });
    }, 1000);
});

// 导出全局函数
window.showMessage = showMessage;
window.formatDate = formatDate;
window.formatPrice = formatPrice;
window.loadData = loadData;
window.submitData = submitData;
window.confirmAction = confirmAction;
