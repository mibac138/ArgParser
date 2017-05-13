# Parser cheat sheet
## Parsers
- `BooleanParser` - translates given input to a boolean.
  - True values are: `yes`, `y`, `true`, `t`, `1`
  - False values are: `no`, `n`, `false`, `f`, `0`
  
  Examples:
  - `0` > `false`
  - 't' > `true`
  - 'n' > `false`
  - `yes` > `true`
- `IntParser` - converts given input to an int (uses `Integer.parseInt`)
  
  Examples:
  - `12` > `12`
  - `1234` > `1234`
- `SequenceParser` - reads given input until hits a space *or* if input started with a recognized quotation mark until it hits another (the same) quotation mark
  - Default quotation marks are: `'`, `"`
  
  Examples:
  - `Hi mate` > `Hi`
  - `"Hi mate"` > `"Hi mate"`
  
## Parser registries
- `SimpleParserRegistry` 
   - Format: `value`
   - Input must be in correct order
   - Example: `value1 value2 value3`
- `NamedParserRegistry`
   - Format: `--name: value`, `--name=value`, allows custom
   - Input can be in any order
   - Example: `--name3: value --name1=value --name2:value`
- `MixedParserRegistry`
   - Format: `value --name: value --name=value`, allows custom (only for named)
   - Named input can be in any order, unnamed must be in correct order
   - Example: `value1 --name2=value value2 --name1:value'