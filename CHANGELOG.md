# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]
### Added
- `:opts` vector to `:native-image` to specify `native-image` CLI arguments
### Changed
- compile all sources and call `native-image` with classpath, instead of building uberjar
- `native-image` flag `ReportUnsupportedElementsAtRuntime` no longer enabled by default
### Fixed
- replace `-` with `_` in `:main` class names when calling `native-image`

## [0.1.0] - 2018-05-20
### Added
- Support for building native images from uberjars via GraalVM
