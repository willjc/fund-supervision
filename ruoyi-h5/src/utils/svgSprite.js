// H5 SVG Icon Sprite Loader
// 动态加载和管理后台相同的SVG sprite机制

// 预定义的SVG图标内容（从复制的管理后台SVG文件中提取）
const svgIcons = {
  // 基础图标
  'chart': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M0 54.857h36.571V128H0V54.857zM91.429 27.43H128V128H91.429V27.429zM45.714 0h36.572v128H45.714V0z"/></svg>',
  'row': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M152 854.856875h325.7146875V237.715625H134.856875v600q0 6.99375 5.0746875 12.0684375T152 854.856875z m737.143125-17.1421875v-600H546.284375v617.1421875H872q6.99375 0 12.0684375-5.07375t5.0746875-12.0684375z m68.5715625-651.429375V837.715625q0 35.3821875-25.16625 60.5484375T872 923.4284375H152q-35.383125 0-60.5484375-25.1653125T66.284375 837.7146875V186.284375q0-35.3821875 25.16625-60.5484375T152 100.5715625h720q35.383125 0 60.5484375 25.1653125t25.16625 60.5484375z"/></svg>',
  'radio': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M64 0a64 64 0 1 0 0 128A64 64 0 0 0 64 0zm0 14a50 50 0 0 1 50 50 50 50 0 0 1-100 0 50 50 0 0 1 50-50zm20 64a20 20 0 0 0-40 0v-10h40v10zm-5-20h-30v-20h30v20z"/></svg>',
  'tool': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M96 32v64H32V32h64m80-8a8 8 0 0 1 8 8v80a8 8 0 0 1-8 8H16a8 8 0 0 1-8-8V32a8 8 0 0 1 8-8h160zM72 64v-16H56v16h16zm48 0v-16h-16v16h16zm-48 24v-16H56v16h16zm48 0v-16h-16v16h16z"/></svg>',
  'monitor': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M16 16h96a16 16 0 0 1 16 16v80a16 16 0 0 1-16 16H16A16 16 0 0 1 0 112V32a16 16 0 0 1 16-16h96zm8 8h-96v80h96V24zm-40 40v-16h-24v16h24zm0 24v-16h-24v16h24z"/></svg>',

  // 数据库中配置的图标
  'dict': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M96 16v96H32V16h64m80-8H16a8 8 0 0 0-8 8v112a8 8 0 0 0 8 8h160a8 8 0 0 0 8-8V16a8 8 0 0 0-8-8z M48 48h32v8H48v-8zm0 16h32v8H48v-8zm0 16h32v8H48v-8z"/></svg>',
  'edit': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M103.896 31.931l-7.827-7.827a8 8 0 0 0-11.314 0l-56.568 56.568a8 8 0 0 0-2.343 5.657v16.97a8 8 0 0 0 8 8h16.971a8 8 0 0 0 5.656-2.343l56.569-56.569a8 8 0 0 0 0-11.314z M32 96h8v8h-8v-8z"/></svg>',
  'lock': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M64 8c19.882 0 36 16.118 36 36v12h8a8 8 0 0 1 8 8v48a8 8 0 0 1-8 8H16a8 8 0 0 1-8-8V72a8 8 0 0 1 8-8h8V44c0-19.882 16.118-36 36-36zm0 16c-11.046 0-20 8.954-20 20v12h40V44c0-11.046-8.954-20-20-20z"/></svg>',
  'rate': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M64 8l18.366 37.188L128 49.154l-28.666 27.936L103.896 120 64 97.654 24.104 120l4.562-42.91L0 49.154l45.634-3.966z"/></svg>',
  'server': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M16 16h96a16 16 0 0 1 16 16v24a16 16 0 0 1-16 16H16A16 16 0 0 1 0 56V32a16 16 0 0 1 16-16h96zm0 48h96a16 16 0 0 1 16 16v24a16 16 0 0 1-16 16H16a16 16 0 0 1-16-16V80a16 16 0 0 1 16-16h96z M16 32v8h96v-8H16zm0 48v8h96v-8H16z"/></svg>',

  // Vant图标直接使用原始名称，不做映射

  'default': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M64 8a56 56 0 1 0 0 112 56 56 56 0 0 0 0-112zm0 16a40 40 0 1 1 0 80 40 40 0 0 1 0-80z"/></svg>',

  // 添加缺失的基础图标
  'shopping': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M32 32h64l8 64H24l8-64zm16 80a8 8 0 1 0 0 16 8 8 0 0 0 0-16zm32 0a8 8 0 1 0 0 16 8 8 0 0 0 0-16z"/></svg>',
  'people': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M96 64a16 16 0 1 0 0-32 16 16 0 0 0 0 32zM32 64a16 16 0 1 0 0-32 16 16 0 0 0 0 32zm64 16a24 24 0 0 1 24 24v24H40v-24a24 24 0 0 1 24-24h32zm-64 0a24 24 0 0 0-24 24v24h16v-24a8 8 0 0 1 8-8z"/></svg>',
  'camera': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M96 32h-16l-8-16H56l-8 16H32a16 16 0 0 0-16 16v48a16 16 0 0 0 16 16h64a16 16 0 0 0 16-16V48a16 16 0 0 0-16-16zM64 80a16 16 0 1 1 0-32 16 16 0 0 1 0 32z"/></svg>',
  'message': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M16 32h96a16 16 0 0 1 16 16v48a16 16 0 0 1-16 16H48l-16 16v-16H16a16 16 0 0 1-16-16V48a16 16 0 0 1 16-16h96z"/></svg>',
  'warning': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M64 8l56 96H8l56-96zm0 32l-8 24h16l-8-24zm0 32a8 8 0 1 0 0 16 8 8 0 0 0 0-16z"/></svg>',
  'user': '<svg width="128" height="128" xmlns="http://www.w3.org/2000/svg"><path d="M64 32a24 24 0 1 0 0-48 24 24 0 0 0 0 48zm0 16c26.51 0 48 17.94 48 40v8H16v-8c0-22.06 21.49-40 48-40z"/></svg>'
}

// 创建SVG sprite
function createSvgSprite() {
  const symbols = Object.entries(svgIcons).map(([id, svg]) => {
    // 提取SVG内容并转换为symbol
    const match = svg.match(/<svg[^>]*>([\s\S]*?)<\/svg>/);
    if (match) {
      // 尝试提取原始viewBox
      let viewBox = '0 0 128 128'; // 默认viewBox
      const viewBoxMatch = svg.match(/viewBox="([^"]+)"/);
      if (viewBoxMatch) {
        viewBox = viewBoxMatch[1];
      }

      const content = match[1];
      return `<symbol id="icon-${id}" viewBox="${viewBox}">${content}</symbol>`;
    }
    return '';
  }).join('\n  ');

  return `
<svg style="position: absolute; width: 0; height: 0; overflow: hidden;" version="1.1" xmlns="http://www.w3.org/2000/svg">
  <defs>
    ${symbols}
  </defs>
</svg>`;
}

// 初始化SVG sprite
function initSvgSprite() {
  // 检查是否已经存在sprite
  if (document.getElementById('svg-sprite')) {
    return;
  }

  const sprite = createSvgSprite();
  const div = document.createElement('div');
  div.innerHTML = sprite;
  div.id = 'svg-sprite';
  div.style.display = 'none';
  document.body.insertBefore(div, document.body.firstChild);

  console.log('SVG Sprite initialized with icons:', Object.keys(svgIcons));
}

// 检查图标是否存在的函数
function hasIcon(iconName) {
  return Object.prototype.hasOwnProperty.call(svgIcons, iconName);
}

// 获取所有可用图标
function getAvailableIcons() {
  return Object.keys(svgIcons);
}

// 导出函数
export {
  initSvgSprite,
  hasIcon,
  getAvailableIcons,
  svgIcons // 导出图标映射，用于调试
};