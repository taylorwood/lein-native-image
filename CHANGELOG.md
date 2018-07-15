# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]

## [0.3.0] - 2018-07-15
### Added
- Support more conventional `GRAALVM_HOME` path format i.e. `$GRAALVM_HOME/bin/native-image`
- Use `GRAALVM_HOME` environment variable if `:graal-bin` is unspecified
### Fixed
- Regression from 0.2.0: the consuming project's `:uberjar` profile was not merged by default. 
  This default behavior can be overriden by specifying a `:native-image` profile.

## [0.2.0] - 2018-05-27
### Added
- `:opts` vector to `:native-image` to specify `native-image` CLI arguments
### Changed
- Compile all sources and call `native-image` with classpath, instead of building uberjar
- `native-image` flag `ReportUnsupportedElementsAtRuntime` no longer enabled by default
### Fixed
- Replace `-` with `_` in `:main` class names when calling `native-image`

## [0.1.0] - 2018-05-20
### Added
- Support for building native images from uberjars via GraalVM
