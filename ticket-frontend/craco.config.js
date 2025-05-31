module.exports = {
  webpack: {
    configure: (webpackConfig) => {
      // Find the babel-loader rule
      const oneOfRule = webpackConfig.module.rules.find((rule) => rule.oneOf);
      if (oneOfRule) {
        const babelLoader = oneOfRule.oneOf.find((rule) => 
          rule.loader && rule.loader.includes('babel-loader')
        );
        
        if (babelLoader && babelLoader.options) {
          // Ensure babel processes node_modules for @mui packages
          babelLoader.include = [
            babelLoader.include,
            /node_modules\/@mui/
          ];
          
          // Add plugin for nullish coalescing operator
          if (!babelLoader.options.plugins) {
            babelLoader.options.plugins = [];
          }
          babelLoader.options.plugins.push(
            '@babel/plugin-proposal-nullish-coalescing-operator'
          );
        }
      }
      
      return webpackConfig;
    },
  },
};
