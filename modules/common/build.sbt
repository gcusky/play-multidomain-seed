import scalariform.formatter.preferences._

Common.moduleSettings("common")

// Add here the specific settings for this module
// 在此添加此模块的特定设置


libraryDependencies ++= Common.commonDependencies

scalariformPreferences := scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(DanglingCloseParenthesis, Preserve)