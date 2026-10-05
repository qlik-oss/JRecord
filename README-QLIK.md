# Qlik changes to third-party sources

## JRecord 0.93.4
- Upstream: https://github.com/bmTas/JRecord
- Qlik branch: `QLIK-1.0.0`

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
