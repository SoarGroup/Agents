Modifications by Dave Ray from original Visual Soar parser

* Changed package
* Changed parser to only parse production bodies, i.e. the text inside the 
  braces.
* Re-generated parser with JavaCC
* Created SoarToken.java and modified Token.create() to return these token
  types. This is so we can store offsets rather than line/column
* Sub-classed SimpleCharStream with SoarCharStream to keep track of offset.
* Sub-classed SoarParserTokenManager to override jjFillToken() to set token
  offsets.
  