import SvgIcon from '@/components/SvgIcon'// svg component

// register globally - will be registered in main.js
export { SvgIcon }

const req = require.context('./svg', false, /\.svg$/)
const requireAll = requireContext => requireContext.keys().map(requireContext)
requireAll(req)