# Qlik changes to third-party sources

## cb2xml 1.01.08
Upstream: https://github.com/bmTas/cb2xml

### Summary of changes
- TODO: high-level highlight 1

### Modified files
- `src/.../SomeFile.java` - TODO: what changed and why
### Added files

## JRecord 0.93.4
Upstream: https://github.com/bmTas/JRecord

### Summary of changes
- HFP convertors: Added converters between ieee 754 and IBM's Hex floating points
- Fixed sized records: Added support for Cobol structures that are written to disk with fixed offsets even if "occurs depending on" is used.

### Modified files
- `src/main/java/net/sf/JRecord/Details/RecordDetail.java` - support for fixed sized cobol records.
- `src/main/java/net/sf/JRecord/Types/TypeFloat.java` - support for HFP converters.
- `src/main/java/net/sf/JRecord/Common/Conversion.java` - support for HFP converters.
### Added files
- `src/main/java/net/sf/converters/CvtConversions.java` - The HFP float and double converters.
- `src/main/java/net/sf/converters/CvtException.java` - Exception that is thrown on HFP conversion errors.
- `src/test/java/net/sf/converters/CvtConversionsTest.java` - Unit tests for the HFP float converters.
