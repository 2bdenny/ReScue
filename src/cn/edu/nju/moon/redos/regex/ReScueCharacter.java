package cn.edu.nju.moon.redos.regex;

import java.lang.Character.UnicodeBlock;
// We just copy this class here because of class dependence
public class ReScueCharacter {
    private static final int blockStarts[] = {
            0x0000,   // 0000..007F; Basic Latin
            0x0080,   // 0080..00FF; Latin-1 Supplement
            0x0100,   // 0100..017F; Latin Extended-A
            0x0180,   // 0180..024F; Latin Extended-B
            0x0250,   // 0250..02AF; IPA Extensions
            0x02B0,   // 02B0..02FF; Spacing Modifier Letters
            0x0300,   // 0300..036F; Combining Diacritical Marks
            0x0370,   // 0370..03FF; Greek and Coptic
            0x0400,   // 0400..04FF; Cyrillic
            0x0500,   // 0500..052F; Cyrillic Supplement
            0x0530,   // 0530..058F; Armenian
            0x0590,   // 0590..05FF; Hebrew
            0x0600,   // 0600..06FF; Arabic
            0x0700,   // 0700..074F; Syriac
            0x0750,   // 0750..077F; Arabic Supplement
            0x0780,   // 0780..07BF; Thaana
            0x07C0,   // 07C0..07FF; NKo
            0x0800,   // 0800..083F; Samaritan
            0x0840,   // 0840..085F; Mandaic
            0x0860,   //             unassigned
            0x08A0,   // 08A0..08FF; Arabic Extended-A
            0x0900,   // 0900..097F; Devanagari
            0x0980,   // 0980..09FF; Bengali
            0x0A00,   // 0A00..0A7F; Gurmukhi
            0x0A80,   // 0A80..0AFF; Gujarati
            0x0B00,   // 0B00..0B7F; Oriya
            0x0B80,   // 0B80..0BFF; Tamil
            0x0C00,   // 0C00..0C7F; Telugu
            0x0C80,   // 0C80..0CFF; Kannada
            0x0D00,   // 0D00..0D7F; Malayalam
            0x0D80,   // 0D80..0DFF; Sinhala
            0x0E00,   // 0E00..0E7F; Thai
            0x0E80,   // 0E80..0EFF; Lao
            0x0F00,   // 0F00..0FFF; Tibetan
            0x1000,   // 1000..109F; Myanmar
            0x10A0,   // 10A0..10FF; Georgian
            0x1100,   // 1100..11FF; Hangul Jamo
            0x1200,   // 1200..137F; Ethiopic
            0x1380,   // 1380..139F; Ethiopic Supplement
            0x13A0,   // 13A0..13FF; Cherokee
            0x1400,   // 1400..167F; Unified Canadian Aboriginal Syllabics
            0x1680,   // 1680..169F; Ogham
            0x16A0,   // 16A0..16FF; Runic
            0x1700,   // 1700..171F; Tagalog
            0x1720,   // 1720..173F; Hanunoo
            0x1740,   // 1740..175F; Buhid
            0x1760,   // 1760..177F; Tagbanwa
            0x1780,   // 1780..17FF; Khmer
            0x1800,   // 1800..18AF; Mongolian
            0x18B0,   // 18B0..18FF; Unified Canadian Aboriginal Syllabics Extended
            0x1900,   // 1900..194F; Limbu
            0x1950,   // 1950..197F; Tai Le
            0x1980,   // 1980..19DF; New Tai Lue
            0x19E0,   // 19E0..19FF; Khmer Symbols
            0x1A00,   // 1A00..1A1F; Buginese
            0x1A20,   // 1A20..1AAF; Tai Tham
            0x1AB0,   //             unassigned
            0x1B00,   // 1B00..1B7F; Balinese
            0x1B80,   // 1B80..1BBF; Sundanese
            0x1BC0,   // 1BC0..1BFF; Batak
            0x1C00,   // 1C00..1C4F; Lepcha
            0x1C50,   // 1C50..1C7F; Ol Chiki
            0x1C80,   //             unassigned
            0x1CC0,   // 1CC0..1CCF; Sundanese Supplement
            0x1CD0,   // 1CD0..1CFF; Vedic Extensions
            0x1D00,   // 1D00..1D7F; Phonetic Extensions
            0x1D80,   // 1D80..1DBF; Phonetic Extensions Supplement
            0x1DC0,   // 1DC0..1DFF; Combining Diacritical Marks Supplement
            0x1E00,   // 1E00..1EFF; Latin Extended Additional
            0x1F00,   // 1F00..1FFF; Greek Extended
            0x2000,   // 2000..206F; General Punctuation
            0x2070,   // 2070..209F; Superscripts and Subscripts
            0x20A0,   // 20A0..20CF; Currency Symbols
            0x20D0,   // 20D0..20FF; Combining Diacritical Marks for Symbols
            0x2100,   // 2100..214F; Letterlike Symbols
            0x2150,   // 2150..218F; Number Forms
            0x2190,   // 2190..21FF; Arrows
            0x2200,   // 2200..22FF; Mathematical Operators
            0x2300,   // 2300..23FF; Miscellaneous Technical
            0x2400,   // 2400..243F; Control Pictures
            0x2440,   // 2440..245F; Optical Character Recognition
            0x2460,   // 2460..24FF; Enclosed Alphanumerics
            0x2500,   // 2500..257F; Box Drawing
            0x2580,   // 2580..259F; Block Elements
            0x25A0,   // 25A0..25FF; Geometric Shapes
            0x2600,   // 2600..26FF; Miscellaneous Symbols
            0x2700,   // 2700..27BF; Dingbats
            0x27C0,   // 27C0..27EF; Miscellaneous Mathematical Symbols-A
            0x27F0,   // 27F0..27FF; Supplemental Arrows-A
            0x2800,   // 2800..28FF; Braille Patterns
            0x2900,   // 2900..297F; Supplemental Arrows-B
            0x2980,   // 2980..29FF; Miscellaneous Mathematical Symbols-B
            0x2A00,   // 2A00..2AFF; Supplemental Mathematical Operators
            0x2B00,   // 2B00..2BFF; Miscellaneous Symbols and Arrows
            0x2C00,   // 2C00..2C5F; Glagolitic
            0x2C60,   // 2C60..2C7F; Latin Extended-C
            0x2C80,   // 2C80..2CFF; Coptic
            0x2D00,   // 2D00..2D2F; Georgian Supplement
            0x2D30,   // 2D30..2D7F; Tifinagh
            0x2D80,   // 2D80..2DDF; Ethiopic Extended
            0x2DE0,   // 2DE0..2DFF; Cyrillic Extended-A
            0x2E00,   // 2E00..2E7F; Supplemental Punctuation
            0x2E80,   // 2E80..2EFF; CJK Radicals Supplement
            0x2F00,   // 2F00..2FDF; Kangxi Radicals
            0x2FE0,   //             unassigned
            0x2FF0,   // 2FF0..2FFF; Ideographic Description Characters
            0x3000,   // 3000..303F; CJK Symbols and Punctuation
            0x3040,   // 3040..309F; Hiragana
            0x30A0,   // 30A0..30FF; Katakana
            0x3100,   // 3100..312F; Bopomofo
            0x3130,   // 3130..318F; Hangul Compatibility Jamo
            0x3190,   // 3190..319F; Kanbun
            0x31A0,   // 31A0..31BF; Bopomofo Extended
            0x31C0,   // 31C0..31EF; CJK Strokes
            0x31F0,   // 31F0..31FF; Katakana Phonetic Extensions
            0x3200,   // 3200..32FF; Enclosed CJK Letters and Months
            0x3300,   // 3300..33FF; CJK Compatibility
            0x3400,   // 3400..4DBF; CJK Unified Ideographs Extension A
            0x4DC0,   // 4DC0..4DFF; Yijing Hexagram Symbols
            0x4E00,   // 4E00..9FFF; CJK Unified Ideographs
            0xA000,   // A000..A48F; Yi Syllables
            0xA490,   // A490..A4CF; Yi Radicals
            0xA4D0,   // A4D0..A4FF; Lisu
            0xA500,   // A500..A63F; Vai
            0xA640,   // A640..A69F; Cyrillic Extended-B
            0xA6A0,   // A6A0..A6FF; Bamum
            0xA700,   // A700..A71F; Modifier Tone Letters
            0xA720,   // A720..A7FF; Latin Extended-D
            0xA800,   // A800..A82F; Syloti Nagri
            0xA830,   // A830..A83F; Common Indic Number Forms
            0xA840,   // A840..A87F; Phags-pa
            0xA880,   // A880..A8DF; Saurashtra
            0xA8E0,   // A8E0..A8FF; Devanagari Extended
            0xA900,   // A900..A92F; Kayah Li
            0xA930,   // A930..A95F; Rejang
            0xA960,   // A960..A97F; Hangul Jamo Extended-A
            0xA980,   // A980..A9DF; Javanese
            0xA9E0,   //             unassigned
            0xAA00,   // AA00..AA5F; Cham
            0xAA60,   // AA60..AA7F; Myanmar Extended-A
            0xAA80,   // AA80..AADF; Tai Viet
            0xAAE0,   // AAE0..AAFF; Meetei Mayek Extensions
            0xAB00,   // AB00..AB2F; Ethiopic Extended-A
            0xAB30,   //             unassigned
            0xABC0,   // ABC0..ABFF; Meetei Mayek
            0xAC00,   // AC00..D7AF; Hangul Syllables
            0xD7B0,   // D7B0..D7FF; Hangul Jamo Extended-B
            0xD800,   // D800..DB7F; High Surrogates
            0xDB80,   // DB80..DBFF; High Private Use Surrogates
            0xDC00,   // DC00..DFFF; Low Surrogates
            0xE000,   // E000..F8FF; Private Use Area
            0xF900,   // F900..FAFF; CJK Compatibility Ideographs
            0xFB00,   // FB00..FB4F; Alphabetic Presentation Forms
            0xFB50,   // FB50..FDFF; Arabic Presentation Forms-A
            0xFE00,   // FE00..FE0F; Variation Selectors
            0xFE10,   // FE10..FE1F; Vertical Forms
            0xFE20,   // FE20..FE2F; Combining Half Marks
            0xFE30,   // FE30..FE4F; CJK Compatibility Forms
            0xFE50,   // FE50..FE6F; Small Form Variants
            0xFE70,   // FE70..FEFF; Arabic Presentation Forms-B
            0xFF00,   // FF00..FFEF; Halfwidth and Fullwidth Forms
            0xFFF0,   // FFF0..FFFF; Specials
            0x10000,  // 10000..1007F; Linear B Syllabary
            0x10080,  // 10080..100FF; Linear B Ideograms
            0x10100,  // 10100..1013F; Aegean Numbers
            0x10140,  // 10140..1018F; Ancient Greek Numbers
            0x10190,  // 10190..101CF; Ancient Symbols
            0x101D0,  // 101D0..101FF; Phaistos Disc
            0x10200,  //               unassigned
            0x10280,  // 10280..1029F; Lycian
            0x102A0,  // 102A0..102DF; Carian
            0x102E0,  //               unassigned
            0x10300,  // 10300..1032F; Old Italic
            0x10330,  // 10330..1034F; Gothic
            0x10350,  //               unassigned
            0x10380,  // 10380..1039F; Ugaritic
            0x103A0,  // 103A0..103DF; Old Persian
            0x103E0,  //               unassigned
            0x10400,  // 10400..1044F; Deseret
            0x10450,  // 10450..1047F; Shavian
            0x10480,  // 10480..104AF; Osmanya
            0x104B0,  //               unassigned
            0x10800,  // 10800..1083F; Cypriot Syllabary
            0x10840,  // 10840..1085F; Imperial Aramaic
            0x10860,  //               unassigned
            0x10900,  // 10900..1091F; Phoenician
            0x10920,  // 10920..1093F; Lydian
            0x10940,  //               unassigned
            0x10980,  // 10980..1099F; Meroitic Hieroglyphs
            0x109A0,  // 109A0..109FF; Meroitic Cursive
            0x10A00,  // 10A00..10A5F; Kharoshthi
            0x10A60,  // 10A60..10A7F; Old South Arabian
            0x10A80,  //               unassigned
            0x10B00,  // 10B00..10B3F; Avestan
            0x10B40,  // 10B40..10B5F; Inscriptional Parthian
            0x10B60,  // 10B60..10B7F; Inscriptional Pahlavi
            0x10B80,  //               unassigned
            0x10C00,  // 10C00..10C4F; Old Turkic
            0x10C50,  //               unassigned
            0x10E60,  // 10E60..10E7F; Rumi Numeral Symbols
            0x10E80,  //               unassigned
            0x11000,  // 11000..1107F; Brahmi
            0x11080,  // 11080..110CF; Kaithi
            0x110D0,  // 110D0..110FF; Sora Sompeng
            0x11100,  // 11100..1114F; Chakma
            0x11150,  //               unassigned
            0x11180,  // 11180..111DF; Sharada
            0x111E0,  //               unassigned
            0x11680,  // 11680..116CF; Takri
            0x116D0,  //               unassigned
            0x12000,  // 12000..123FF; Cuneiform
            0x12400,  // 12400..1247F; Cuneiform Numbers and Punctuation
            0x12480,  //               unassigned
            0x13000,  // 13000..1342F; Egyptian Hieroglyphs
            0x13430,  //               unassigned
            0x16800,  // 16800..16A3F; Bamum Supplement
            0x16A40,  //               unassigned
            0x16F00,  // 16F00..16F9F; Miao
            0x16FA0,  //               unassigned
            0x1B000,  // 1B000..1B0FF; Kana Supplement
            0x1B100,  //               unassigned
            0x1D000,  // 1D000..1D0FF; Byzantine Musical Symbols
            0x1D100,  // 1D100..1D1FF; Musical Symbols
            0x1D200,  // 1D200..1D24F; Ancient Greek Musical Notation
            0x1D250,  //               unassigned
            0x1D300,  // 1D300..1D35F; Tai Xuan Jing Symbols
            0x1D360,  // 1D360..1D37F; Counting Rod Numerals
            0x1D380,  //               unassigned
            0x1D400,  // 1D400..1D7FF; Mathematical Alphanumeric Symbols
            0x1D800,  //               unassigned
            0x1EE00,  // 1EE00..1EEFF; Arabic Mathematical Alphabetic Symbols
            0x1EF00,  //               unassigned
            0x1F000,  // 1F000..1F02F; Mahjong Tiles
            0x1F030,  // 1F030..1F09F; Domino Tiles
            0x1F0A0,  // 1F0A0..1F0FF; Playing Cards
            0x1F100,  // 1F100..1F1FF; Enclosed Alphanumeric Supplement
            0x1F200,  // 1F200..1F2FF; Enclosed Ideographic Supplement
            0x1F300,  // 1F300..1F5FF; Miscellaneous Symbols And Pictographs
            0x1F600,  // 1F600..1F64F; Emoticons
            0x1F650,  //               unassigned
            0x1F680,  // 1F680..1F6FF; Transport And Map Symbols
            0x1F700,  // 1F700..1F77F; Alchemical Symbols
            0x1F780,  //               unassigned
            0x20000,  // 20000..2A6DF; CJK Unified Ideographs Extension B
            0x2A6E0,  //               unassigned
            0x2A700,  // 2A700..2B73F; CJK Unified Ideographs Extension C
            0x2B740,  // 2B740..2B81F; CJK Unified Ideographs Extension D
            0x2B820,  //               unassigned
            0x2F800,  // 2F800..2FA1F; CJK Compatibility Ideographs Supplement
            0x2FA20,  //               unassigned
            0xE0000,  // E0000..E007F; Tags
            0xE0080,  //               unassigned
            0xE0100,  // E0100..E01EF; Variation Selectors Supplement
            0xE01F0,  //               unassigned
            0xF0000,  // F0000..FFFFF; Supplementary Private Use Area-A
            0x100000  // 100000..10FFFF; Supplementary Private Use Area-B
        };

    private static final UnicodeBlock[] blocks = {
        UnicodeBlock.BASIC_LATIN,
        UnicodeBlock.LATIN_1_SUPPLEMENT,
    UnicodeBlock.LATIN_EXTENDED_A,
    UnicodeBlock.LATIN_EXTENDED_B,
    UnicodeBlock.IPA_EXTENSIONS,
    UnicodeBlock.SPACING_MODIFIER_LETTERS,
    UnicodeBlock.COMBINING_DIACRITICAL_MARKS,
    UnicodeBlock.GREEK,
    UnicodeBlock.CYRILLIC,
    UnicodeBlock.CYRILLIC_SUPPLEMENTARY,
    UnicodeBlock.ARMENIAN,
    UnicodeBlock.HEBREW,
    UnicodeBlock.ARABIC,
    UnicodeBlock.SYRIAC,
    UnicodeBlock.ARABIC_SUPPLEMENT,
    UnicodeBlock.THAANA,
    UnicodeBlock.NKO,
    UnicodeBlock.SAMARITAN,
    UnicodeBlock.MANDAIC,
    null,
    UnicodeBlock.ARABIC_EXTENDED_A,
    UnicodeBlock.DEVANAGARI,
    UnicodeBlock.BENGALI,
    UnicodeBlock.GURMUKHI,
    UnicodeBlock.GUJARATI,
    UnicodeBlock.ORIYA,
    UnicodeBlock.TAMIL,
    UnicodeBlock.TELUGU,
    UnicodeBlock.KANNADA,
    UnicodeBlock.MALAYALAM,
    UnicodeBlock.SINHALA,
    UnicodeBlock.THAI,
    UnicodeBlock.LAO,
    UnicodeBlock.TIBETAN,
    UnicodeBlock.MYANMAR,
    UnicodeBlock.GEORGIAN,
    UnicodeBlock.HANGUL_JAMO,
    UnicodeBlock.ETHIOPIC,
    UnicodeBlock.ETHIOPIC_SUPPLEMENT,
    UnicodeBlock.CHEROKEE,
    UnicodeBlock.UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS,
    UnicodeBlock.OGHAM,
    UnicodeBlock.RUNIC,
    UnicodeBlock.TAGALOG,
    UnicodeBlock.HANUNOO,
    UnicodeBlock.BUHID,
    UnicodeBlock.TAGBANWA,
    UnicodeBlock.KHMER,
    UnicodeBlock.MONGOLIAN,
    UnicodeBlock.UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED,
    UnicodeBlock.LIMBU,
    UnicodeBlock.TAI_LE,
    UnicodeBlock.NEW_TAI_LUE,
    UnicodeBlock.KHMER_SYMBOLS,
    UnicodeBlock.BUGINESE,
    UnicodeBlock.TAI_THAM,
    null,
    UnicodeBlock.BALINESE,
    UnicodeBlock.SUNDANESE,
    UnicodeBlock.BATAK,
    UnicodeBlock.LEPCHA,
    UnicodeBlock.OL_CHIKI,
    null,
    UnicodeBlock.SUNDANESE_SUPPLEMENT,
    UnicodeBlock.VEDIC_EXTENSIONS,
    UnicodeBlock.PHONETIC_EXTENSIONS,
    UnicodeBlock.PHONETIC_EXTENSIONS_SUPPLEMENT,
    UnicodeBlock.COMBINING_DIACRITICAL_MARKS_SUPPLEMENT,
    UnicodeBlock.LATIN_EXTENDED_ADDITIONAL,
    UnicodeBlock.GREEK_EXTENDED,
    UnicodeBlock.GENERAL_PUNCTUATION,
    UnicodeBlock.SUPERSCRIPTS_AND_SUBSCRIPTS,
    UnicodeBlock.CURRENCY_SYMBOLS,
    UnicodeBlock.COMBINING_MARKS_FOR_SYMBOLS,
    UnicodeBlock.LETTERLIKE_SYMBOLS,
    UnicodeBlock.NUMBER_FORMS,
    UnicodeBlock.ARROWS,
    UnicodeBlock.MATHEMATICAL_OPERATORS,
    UnicodeBlock.MISCELLANEOUS_TECHNICAL,
    UnicodeBlock.CONTROL_PICTURES,
    UnicodeBlock.OPTICAL_CHARACTER_RECOGNITION,
    UnicodeBlock.ENCLOSED_ALPHANUMERICS,
    UnicodeBlock.BOX_DRAWING,
    UnicodeBlock.BLOCK_ELEMENTS,
    UnicodeBlock.GEOMETRIC_SHAPES,
    UnicodeBlock.MISCELLANEOUS_SYMBOLS,
    UnicodeBlock.DINGBATS,
    UnicodeBlock.MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A,
    UnicodeBlock.SUPPLEMENTAL_ARROWS_A,
    UnicodeBlock.BRAILLE_PATTERNS,
    UnicodeBlock.SUPPLEMENTAL_ARROWS_B,
    UnicodeBlock.MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B,
    UnicodeBlock.SUPPLEMENTAL_MATHEMATICAL_OPERATORS,
    UnicodeBlock.MISCELLANEOUS_SYMBOLS_AND_ARROWS,
    UnicodeBlock.GLAGOLITIC,
    UnicodeBlock.LATIN_EXTENDED_C,
    UnicodeBlock.COPTIC,
    UnicodeBlock.GEORGIAN_SUPPLEMENT,
    UnicodeBlock.TIFINAGH,
    UnicodeBlock.ETHIOPIC_EXTENDED,
    UnicodeBlock.CYRILLIC_EXTENDED_A,
    UnicodeBlock.SUPPLEMENTAL_PUNCTUATION,
    UnicodeBlock.CJK_RADICALS_SUPPLEMENT,
    UnicodeBlock.KANGXI_RADICALS,
    null,
    UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS,
    UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION,
    UnicodeBlock.HIRAGANA,
    UnicodeBlock.KATAKANA,
    UnicodeBlock.BOPOMOFO,
    UnicodeBlock.HANGUL_COMPATIBILITY_JAMO,
    UnicodeBlock.KANBUN,
    UnicodeBlock.BOPOMOFO_EXTENDED,
    UnicodeBlock.CJK_STROKES,
    UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS,
    UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS,
    UnicodeBlock.CJK_COMPATIBILITY,
    UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A,
    UnicodeBlock.YIJING_HEXAGRAM_SYMBOLS,
    UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS,
    UnicodeBlock.YI_SYLLABLES,
    UnicodeBlock.YI_RADICALS,
    UnicodeBlock.LISU,
    UnicodeBlock.VAI,
    UnicodeBlock.CYRILLIC_EXTENDED_B,
    UnicodeBlock.BAMUM,
    UnicodeBlock.MODIFIER_TONE_LETTERS,
    UnicodeBlock.LATIN_EXTENDED_D,
    UnicodeBlock.SYLOTI_NAGRI,
    UnicodeBlock.COMMON_INDIC_NUMBER_FORMS,
    UnicodeBlock.PHAGS_PA,
    UnicodeBlock.SAURASHTRA,
    UnicodeBlock.DEVANAGARI_EXTENDED,
    UnicodeBlock.KAYAH_LI,
    UnicodeBlock.REJANG,
    UnicodeBlock.HANGUL_JAMO_EXTENDED_A,
    UnicodeBlock.JAVANESE,
    null,
    UnicodeBlock.CHAM,
    UnicodeBlock.MYANMAR_EXTENDED_A,
    UnicodeBlock.TAI_VIET,
    UnicodeBlock.MEETEI_MAYEK_EXTENSIONS,
    UnicodeBlock.ETHIOPIC_EXTENDED_A,
    null,
    UnicodeBlock.MEETEI_MAYEK,
    UnicodeBlock.HANGUL_SYLLABLES,
    UnicodeBlock.HANGUL_JAMO_EXTENDED_B,
    UnicodeBlock.HIGH_SURROGATES,
    UnicodeBlock.HIGH_PRIVATE_USE_SURROGATES,
    UnicodeBlock.LOW_SURROGATES,
    UnicodeBlock.PRIVATE_USE_AREA,
    UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS,
    UnicodeBlock.ALPHABETIC_PRESENTATION_FORMS,
    UnicodeBlock.ARABIC_PRESENTATION_FORMS_A,
    UnicodeBlock.VARIATION_SELECTORS,
    UnicodeBlock.VERTICAL_FORMS,
    UnicodeBlock.COMBINING_HALF_MARKS,
    UnicodeBlock.CJK_COMPATIBILITY_FORMS,
    UnicodeBlock.SMALL_FORM_VARIANTS,
    UnicodeBlock.ARABIC_PRESENTATION_FORMS_B,
    UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS,
    UnicodeBlock.SPECIALS,
    UnicodeBlock.LINEAR_B_SYLLABARY,
    UnicodeBlock.LINEAR_B_IDEOGRAMS,
    UnicodeBlock.AEGEAN_NUMBERS,
    UnicodeBlock.ANCIENT_GREEK_NUMBERS,
    UnicodeBlock.ANCIENT_SYMBOLS,
    UnicodeBlock.PHAISTOS_DISC,
    null,
    UnicodeBlock.LYCIAN,
    UnicodeBlock.CARIAN,
    null,
    UnicodeBlock.OLD_ITALIC,
    UnicodeBlock.GOTHIC,
    null,
    UnicodeBlock.UGARITIC,
    UnicodeBlock.OLD_PERSIAN,
    null,
    UnicodeBlock.DESERET,
    UnicodeBlock.SHAVIAN,
    UnicodeBlock.OSMANYA,
    null,
    UnicodeBlock.CYPRIOT_SYLLABARY,
    UnicodeBlock.IMPERIAL_ARAMAIC,
    null,
    UnicodeBlock.PHOENICIAN,
    UnicodeBlock.LYDIAN,
    null,
    UnicodeBlock.MEROITIC_HIEROGLYPHS,
    UnicodeBlock.MEROITIC_CURSIVE,
    UnicodeBlock.KHAROSHTHI,
    UnicodeBlock.OLD_SOUTH_ARABIAN,
    null,
    UnicodeBlock.AVESTAN,
    UnicodeBlock.INSCRIPTIONAL_PARTHIAN,
    UnicodeBlock.INSCRIPTIONAL_PAHLAVI,
    null,
    UnicodeBlock.OLD_TURKIC,
    null,
    UnicodeBlock.RUMI_NUMERAL_SYMBOLS,
    null,
    UnicodeBlock.BRAHMI,
    UnicodeBlock.KAITHI,
    UnicodeBlock.SORA_SOMPENG,
    UnicodeBlock.CHAKMA,
    null,
    UnicodeBlock.SHARADA,
    null,
    UnicodeBlock.TAKRI,
    null,
    UnicodeBlock.CUNEIFORM,
    UnicodeBlock.CUNEIFORM_NUMBERS_AND_PUNCTUATION,
    null,
    UnicodeBlock.EGYPTIAN_HIEROGLYPHS,
    null,
    UnicodeBlock.BAMUM_SUPPLEMENT,
    null,
    UnicodeBlock.MIAO,
    null,
    UnicodeBlock.KANA_SUPPLEMENT,
    null,
    UnicodeBlock.BYZANTINE_MUSICAL_SYMBOLS,
    UnicodeBlock.MUSICAL_SYMBOLS,
    UnicodeBlock.ANCIENT_GREEK_MUSICAL_NOTATION,
    null,
    UnicodeBlock.TAI_XUAN_JING_SYMBOLS,
    UnicodeBlock.COUNTING_ROD_NUMERALS,
    null,
    UnicodeBlock.MATHEMATICAL_ALPHANUMERIC_SYMBOLS,
    null,
    UnicodeBlock.ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS,
    null,
    UnicodeBlock.MAHJONG_TILES,
    UnicodeBlock.DOMINO_TILES,
    UnicodeBlock.PLAYING_CARDS,
    UnicodeBlock.ENCLOSED_ALPHANUMERIC_SUPPLEMENT,
    UnicodeBlock.ENCLOSED_IDEOGRAPHIC_SUPPLEMENT,
    UnicodeBlock.MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS,
    UnicodeBlock.EMOTICONS,
    null,
    UnicodeBlock.TRANSPORT_AND_MAP_SYMBOLS,
    UnicodeBlock.ALCHEMICAL_SYMBOLS,
    null,
    UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B,
    null,
    UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C,
    UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D,
    null,
    UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT,
    null,
    UnicodeBlock.TAGS,
    null,
    UnicodeBlock.VARIATION_SELECTORS_SUPPLEMENT,
    null,
    UnicodeBlock.SUPPLEMENTARY_PRIVATE_USE_AREA_A,
    UnicodeBlock.SUPPLEMENTARY_PRIVATE_USE_AREA_B
    };

    /**
     * Returns the object representing the Unicode block
     * containing the given character (Unicode code point), or
     * {@code null} if the character is not a member of a
     * defined block.
     *
     * @param   codePoint the character (Unicode code point) in question.
     * @return  The {@code UnicodeBlock} instance representing the
     *          Unicode block of which this character is a member, or
     *          {@code null} if the character is not a member of any
     *          Unicode block
     * @exception IllegalArgumentException if the specified
     * {@code codePoint} is an invalid Unicode code point.
     * @see Character#isValidCodePoint(int)
     * @since   1.5
     */
    public static String oneOfBlock(UnicodeBlock block) {
    	for (int i = 0; i < blocks.length; i++) {
    		if (blocks[i] == block) {
    			int code = blockStarts[i];
    			return new String(Character.toChars(code));
    		}
    	}
    	return "";
    }
    
    
	public static enum UnicodeScript {
		/**
		 * Unicode script "Common".
		 */
		COMMON,

		/**
		 * Unicode script "Latin".
		 */
		LATIN,

		/**
		 * Unicode script "Greek".
		 */
		GREEK,

		/**
		 * Unicode script "Cyrillic".
		 */
		CYRILLIC,

		/**
		 * Unicode script "Armenian".
		 */
		ARMENIAN,

		/**
		 * Unicode script "Hebrew".
		 */
		HEBREW,

		/**
		 * Unicode script "Arabic".
		 */
		ARABIC,

		/**
		 * Unicode script "Syriac".
		 */
		SYRIAC,

		/**
		 * Unicode script "Thaana".
		 */
		THAANA,

		/**
		 * Unicode script "Devanagari".
		 */
		DEVANAGARI,

		/**
		 * Unicode script "Bengali".
		 */
		BENGALI,

		/**
		 * Unicode script "Gurmukhi".
		 */
		GURMUKHI,

		/**
		 * Unicode script "Gujarati".
		 */
		GUJARATI,

		/**
		 * Unicode script "Oriya".
		 */
		ORIYA,

		/**
		 * Unicode script "Tamil".
		 */
		TAMIL,

		/**
		 * Unicode script "Telugu".
		 */
		TELUGU,

		/**
		 * Unicode script "Kannada".
		 */
		KANNADA,

		/**
		 * Unicode script "Malayalam".
		 */
		MALAYALAM,

		/**
		 * Unicode script "Sinhala".
		 */
		SINHALA,

		/**
		 * Unicode script "Thai".
		 */
		THAI,

		/**
		 * Unicode script "Lao".
		 */
		LAO,

		/**
		 * Unicode script "Tibetan".
		 */
		TIBETAN,

		/**
		 * Unicode script "Myanmar".
		 */
		MYANMAR,

		/**
		 * Unicode script "Georgian".
		 */
		GEORGIAN,

		/**
		 * Unicode script "Hangul".
		 */
		HANGUL,

		/**
		 * Unicode script "Ethiopic".
		 */
		ETHIOPIC,

		/**
		 * Unicode script "Cherokee".
		 */
		CHEROKEE,

		/**
		 * Unicode script "Canadian_Aboriginal".
		 */
		CANADIAN_ABORIGINAL,

		/**
		 * Unicode script "Ogham".
		 */
		OGHAM,

		/**
		 * Unicode script "Runic".
		 */
		RUNIC,

		/**
		 * Unicode script "Khmer".
		 */
		KHMER,

		/**
		 * Unicode script "Mongolian".
		 */
		MONGOLIAN,

		/**
		 * Unicode script "Hiragana".
		 */
		HIRAGANA,

		/**
		 * Unicode script "Katakana".
		 */
		KATAKANA,

		/**
		 * Unicode script "Bopomofo".
		 */
		BOPOMOFO,

		/**
		 * Unicode script "Han".
		 */
		HAN,

		/**
		 * Unicode script "Yi".
		 */
		YI,

		/**
		 * Unicode script "Old_Italic".
		 */
		OLD_ITALIC,

		/**
		 * Unicode script "Gothic".
		 */
		GOTHIC,

		/**
		 * Unicode script "Deseret".
		 */
		DESERET,

		/**
		 * Unicode script "Inherited".
		 */
		INHERITED,

		/**
		 * Unicode script "Tagalog".
		 */
		TAGALOG,

		/**
		 * Unicode script "Hanunoo".
		 */
		HANUNOO,

		/**
		 * Unicode script "Buhid".
		 */
		BUHID,

		/**
		 * Unicode script "Tagbanwa".
		 */
		TAGBANWA,

		/**
		 * Unicode script "Limbu".
		 */
		LIMBU,

		/**
		 * Unicode script "Tai_Le".
		 */
		TAI_LE,

		/**
		 * Unicode script "Linear_B".
		 */
		LINEAR_B,

		/**
		 * Unicode script "Ugaritic".
		 */
		UGARITIC,

		/**
		 * Unicode script "Shavian".
		 */
		SHAVIAN,

		/**
		 * Unicode script "Osmanya".
		 */
		OSMANYA,

		/**
		 * Unicode script "Cypriot".
		 */
		CYPRIOT,

		/**
		 * Unicode script "Braille".
		 */
		BRAILLE,

		/**
		 * Unicode script "Buginese".
		 */
		BUGINESE,

		/**
		 * Unicode script "Coptic".
		 */
		COPTIC,

		/**
		 * Unicode script "New_Tai_Lue".
		 */
		NEW_TAI_LUE,

		/**
		 * Unicode script "Glagolitic".
		 */
		GLAGOLITIC,

		/**
		 * Unicode script "Tifinagh".
		 */
		TIFINAGH,

		/**
		 * Unicode script "Syloti_Nagri".
		 */
		SYLOTI_NAGRI,

		/**
		 * Unicode script "Old_Persian".
		 */
		OLD_PERSIAN,

		/**
		 * Unicode script "Kharoshthi".
		 */
		KHAROSHTHI,

		/**
		 * Unicode script "Balinese".
		 */
		BALINESE,

		/**
		 * Unicode script "Cuneiform".
		 */
		CUNEIFORM,

		/**
		 * Unicode script "Phoenician".
		 */
		PHOENICIAN,

		/**
		 * Unicode script "Phags_Pa".
		 */
		PHAGS_PA,

		/**
		 * Unicode script "Nko".
		 */
		NKO,

		/**
		 * Unicode script "Sundanese".
		 */
		SUNDANESE,

		/**
		 * Unicode script "Batak".
		 */
		BATAK,

		/**
		 * Unicode script "Lepcha".
		 */
		LEPCHA,

		/**
		 * Unicode script "Ol_Chiki".
		 */
		OL_CHIKI,

		/**
		 * Unicode script "Vai".
		 */
		VAI,

		/**
		 * Unicode script "Saurashtra".
		 */
		SAURASHTRA,

		/**
		 * Unicode script "Kayah_Li".
		 */
		KAYAH_LI,

		/**
		 * Unicode script "Rejang".
		 */
		REJANG,

		/**
		 * Unicode script "Lycian".
		 */
		LYCIAN,

		/**
		 * Unicode script "Carian".
		 */
		CARIAN,

		/**
		 * Unicode script "Lydian".
		 */
		LYDIAN,

		/**
		 * Unicode script "Cham".
		 */
		CHAM,

		/**
		 * Unicode script "Tai_Tham".
		 */
		TAI_THAM,

		/**
		 * Unicode script "Tai_Viet".
		 */
		TAI_VIET,

		/**
		 * Unicode script "Avestan".
		 */
		AVESTAN,

		/**
		 * Unicode script "Egyptian_Hieroglyphs".
		 */
		EGYPTIAN_HIEROGLYPHS,

		/**
		 * Unicode script "Samaritan".
		 */
		SAMARITAN,

		/**
		 * Unicode script "Mandaic".
		 */
		MANDAIC,

		/**
		 * Unicode script "Lisu".
		 */
		LISU,

		/**
		 * Unicode script "Bamum".
		 */
		BAMUM,

		/**
		 * Unicode script "Javanese".
		 */
		JAVANESE,

		/**
		 * Unicode script "Meetei_Mayek".
		 */
		MEETEI_MAYEK,

		/**
		 * Unicode script "Imperial_Aramaic".
		 */
		IMPERIAL_ARAMAIC,

		/**
		 * Unicode script "Old_South_Arabian".
		 */
		OLD_SOUTH_ARABIAN,

		/**
		 * Unicode script "Inscriptional_Parthian".
		 */
		INSCRIPTIONAL_PARTHIAN,

		/**
		 * Unicode script "Inscriptional_Pahlavi".
		 */
		INSCRIPTIONAL_PAHLAVI,

		/**
		 * Unicode script "Old_Turkic".
		 */
		OLD_TURKIC,

		/**
		 * Unicode script "Brahmi".
		 */
		BRAHMI,

		/**
		 * Unicode script "Kaithi".
		 */
		KAITHI,

		/**
		 * Unicode script "Meroitic Hieroglyphs".
		 */
		MEROITIC_HIEROGLYPHS,

		/**
		 * Unicode script "Meroitic Cursive".
		 */
		MEROITIC_CURSIVE,

		/**
		 * Unicode script "Sora Sompeng".
		 */
		SORA_SOMPENG,

		/**
		 * Unicode script "Chakma".
		 */
		CHAKMA,

		/**
		 * Unicode script "Sharada".
		 */
		SHARADA,

		/**
		 * Unicode script "Takri".
		 */
		TAKRI,

		/**
		 * Unicode script "Miao".
		 */
		MIAO,

		/**
		 * Unicode script "Unknown".
		 */
		UNKNOWN;

        private static final int[] scriptStarts = {
            0x0000,   // 0000..0040; COMMON
            0x0041,   // 0041..005A; LATIN
            0x005B,   // 005B..0060; COMMON
            0x0061,   // 0061..007A; LATIN
            0x007B,   // 007B..00A9; COMMON
            0x00AA,   // 00AA..00AA; LATIN
            0x00AB,   // 00AB..00B9; COMMON
            0x00BA,   // 00BA..00BA; LATIN
            0x00BB,   // 00BB..00BF; COMMON
            0x00C0,   // 00C0..00D6; LATIN
            0x00D7,   // 00D7..00D7; COMMON
            0x00D8,   // 00D8..00F6; LATIN
            0x00F7,   // 00F7..00F7; COMMON
            0x00F8,   // 00F8..02B8; LATIN
            0x02B9,   // 02B9..02DF; COMMON
            0x02E0,   // 02E0..02E4; LATIN
            0x02E5,   // 02E5..02E9; COMMON
            0x02EA,   // 02EA..02EB; BOPOMOFO
            0x02EC,   // 02EC..02FF; COMMON
            0x0300,   // 0300..036F; INHERITED
            0x0370,   // 0370..0373; GREEK
            0x0374,   // 0374..0374; COMMON
            0x0375,   // 0375..037D; GREEK
            0x037E,   // 037E..0383; COMMON
            0x0384,   // 0384..0384; GREEK
            0x0385,   // 0385..0385; COMMON
            0x0386,   // 0386..0386; GREEK
            0x0387,   // 0387..0387; COMMON
            0x0388,   // 0388..03E1; GREEK
            0x03E2,   // 03E2..03EF; COPTIC
            0x03F0,   // 03F0..03FF; GREEK
            0x0400,   // 0400..0484; CYRILLIC
            0x0485,   // 0485..0486; INHERITED
            0x0487,   // 0487..0530; CYRILLIC
            0x0531,   // 0531..0588; ARMENIAN
            0x0589,   // 0589..0589; COMMON
            0x058A,   // 058A..0590; ARMENIAN
            0x0591,   // 0591..05FF; HEBREW
            0x0600,   // 0600..060B; ARABIC
            0x060C,   // 060C..060C; COMMON
            0x060D,   // 060D..061A; ARABIC
            0x061B,   // 061B..061D; COMMON
            0x061E,   // 061E..061E; ARABIC
            0x061F,   // 061F..061F; COMMON
            0x0620,   // 0620..063F; ARABIC
            0x0640,   // 0640..0640; COMMON
            0x0641,   // 0641..064A; ARABIC
            0x064B,   // 064B..0655; INHERITED
            0x0656,   // 0656..065F; ARABIC
            0x0660,   // 0660..0669; COMMON
            0x066A,   // 066A..066F; ARABIC
            0x0670,   // 0670..0670; INHERITED
            0x0671,   // 0671..06DC; ARABIC
            0x06DD,   // 06DD..06DD; COMMON
            0x06DE,   // 06DE..06FF; ARABIC
            0x0700,   // 0700..074F; SYRIAC
            0x0750,   // 0750..077F; ARABIC
            0x0780,   // 0780..07BF; THAANA
            0x07C0,   // 07C0..07FF; NKO
            0x0800,   // 0800..083F; SAMARITAN
            0x0840,   // 0840..089F; MANDAIC
            0x08A0,   // 08A0..08FF; ARABIC
            0x0900,   // 0900..0950; DEVANAGARI
            0x0951,   // 0951..0952; INHERITED
            0x0953,   // 0953..0963; DEVANAGARI
            0x0964,   // 0964..0965; COMMON
            0x0966,   // 0966..0980; DEVANAGARI
            0x0981,   // 0981..0A00; BENGALI
            0x0A01,   // 0A01..0A80; GURMUKHI
            0x0A81,   // 0A81..0B00; GUJARATI
            0x0B01,   // 0B01..0B81; ORIYA
            0x0B82,   // 0B82..0C00; TAMIL
            0x0C01,   // 0C01..0C81; TELUGU
            0x0C82,   // 0C82..0CF0; KANNADA
            0x0D02,   // 0D02..0D81; MALAYALAM
            0x0D82,   // 0D82..0E00; SINHALA
            0x0E01,   // 0E01..0E3E; THAI
            0x0E3F,   // 0E3F..0E3F; COMMON
            0x0E40,   // 0E40..0E80; THAI
            0x0E81,   // 0E81..0EFF; LAO
            0x0F00,   // 0F00..0FD4; TIBETAN
            0x0FD5,   // 0FD5..0FD8; COMMON
            0x0FD9,   // 0FD9..0FFF; TIBETAN
            0x1000,   // 1000..109F; MYANMAR
            0x10A0,   // 10A0..10FA; GEORGIAN
            0x10FB,   // 10FB..10FB; COMMON
            0x10FC,   // 10FC..10FF; GEORGIAN
            0x1100,   // 1100..11FF; HANGUL
            0x1200,   // 1200..139F; ETHIOPIC
            0x13A0,   // 13A0..13FF; CHEROKEE
            0x1400,   // 1400..167F; CANADIAN_ABORIGINAL
            0x1680,   // 1680..169F; OGHAM
            0x16A0,   // 16A0..16EA; RUNIC
            0x16EB,   // 16EB..16ED; COMMON
            0x16EE,   // 16EE..16FF; RUNIC
            0x1700,   // 1700..171F; TAGALOG
            0x1720,   // 1720..1734; HANUNOO
            0x1735,   // 1735..173F; COMMON
            0x1740,   // 1740..175F; BUHID
            0x1760,   // 1760..177F; TAGBANWA
            0x1780,   // 1780..17FF; KHMER
            0x1800,   // 1800..1801; MONGOLIAN
            0x1802,   // 1802..1803; COMMON
            0x1804,   // 1804..1804; MONGOLIAN
            0x1805,   // 1805..1805; COMMON
            0x1806,   // 1806..18AF; MONGOLIAN
            0x18B0,   // 18B0..18FF; CANADIAN_ABORIGINAL
            0x1900,   // 1900..194F; LIMBU
            0x1950,   // 1950..197F; TAI_LE
            0x1980,   // 1980..19DF; NEW_TAI_LUE
            0x19E0,   // 19E0..19FF; KHMER
            0x1A00,   // 1A00..1A1F; BUGINESE
            0x1A20,   // 1A20..1AFF; TAI_THAM
            0x1B00,   // 1B00..1B7F; BALINESE
            0x1B80,   // 1B80..1BBF; SUNDANESE
            0x1BC0,   // 1BC0..1BFF; BATAK
            0x1C00,   // 1C00..1C4F; LEPCHA
            0x1C50,   // 1C50..1CBF; OL_CHIKI
            0x1CC0,   // 1CC0..1CCF; SUNDANESE
            0x1CD0,   // 1CD0..1CD2; INHERITED
            0x1CD3,   // 1CD3..1CD3; COMMON
            0x1CD4,   // 1CD4..1CE0; INHERITED
            0x1CE1,   // 1CE1..1CE1; COMMON
            0x1CE2,   // 1CE2..1CE8; INHERITED
            0x1CE9,   // 1CE9..1CEC; COMMON
            0x1CED,   // 1CED..1CED; INHERITED
            0x1CEE,   // 1CEE..1CF3; COMMON
            0x1CF4,   // 1CF4..1CF4; INHERITED
            0x1CF5,   // 1CF5..1CFF; COMMON
            0x1D00,   // 1D00..1D25; LATIN
            0x1D26,   // 1D26..1D2A; GREEK
            0x1D2B,   // 1D2B..1D2B; CYRILLIC
            0x1D2C,   // 1D2C..1D5C; LATIN
            0x1D5D,   // 1D5D..1D61; GREEK
            0x1D62,   // 1D62..1D65; LATIN
            0x1D66,   // 1D66..1D6A; GREEK
            0x1D6B,   // 1D6B..1D77; LATIN
            0x1D78,   // 1D78..1D78; CYRILLIC
            0x1D79,   // 1D79..1DBE; LATIN
            0x1DBF,   // 1DBF..1DBF; GREEK
            0x1DC0,   // 1DC0..1DFF; INHERITED
            0x1E00,   // 1E00..1EFF; LATIN
            0x1F00,   // 1F00..1FFF; GREEK
            0x2000,   // 2000..200B; COMMON
            0x200C,   // 200C..200D; INHERITED
            0x200E,   // 200E..2070; COMMON
            0x2071,   // 2071..2073; LATIN
            0x2074,   // 2074..207E; COMMON
            0x207F,   // 207F..207F; LATIN
            0x2080,   // 2080..208F; COMMON
            0x2090,   // 2090..209F; LATIN
            0x20A0,   // 20A0..20CF; COMMON
            0x20D0,   // 20D0..20FF; INHERITED
            0x2100,   // 2100..2125; COMMON
            0x2126,   // 2126..2126; GREEK
            0x2127,   // 2127..2129; COMMON
            0x212A,   // 212A..212B; LATIN
            0x212C,   // 212C..2131; COMMON
            0x2132,   // 2132..2132; LATIN
            0x2133,   // 2133..214D; COMMON
            0x214E,   // 214E..214E; LATIN
            0x214F,   // 214F..215F; COMMON
            0x2160,   // 2160..2188; LATIN
            0x2189,   // 2189..27FF; COMMON
            0x2800,   // 2800..28FF; BRAILLE
            0x2900,   // 2900..2BFF; COMMON
            0x2C00,   // 2C00..2C5F; GLAGOLITIC
            0x2C60,   // 2C60..2C7F; LATIN
            0x2C80,   // 2C80..2CFF; COPTIC
            0x2D00,   // 2D00..2D2F; GEORGIAN
            0x2D30,   // 2D30..2D7F; TIFINAGH
            0x2D80,   // 2D80..2DDF; ETHIOPIC
            0x2DE0,   // 2DE0..2DFF; CYRILLIC
            0x2E00,   // 2E00..2E7F; COMMON
            0x2E80,   // 2E80..2FEF; HAN
            0x2FF0,   // 2FF0..3004; COMMON
            0x3005,   // 3005..3005; HAN
            0x3006,   // 3006..3006; COMMON
            0x3007,   // 3007..3007; HAN
            0x3008,   // 3008..3020; COMMON
            0x3021,   // 3021..3029; HAN
            0x302A,   // 302A..302D; INHERITED
            0x302E,   // 302E..302F; HANGUL
            0x3030,   // 3030..3037; COMMON
            0x3038,   // 3038..303B; HAN
            0x303C,   // 303C..3040; COMMON
            0x3041,   // 3041..3098; HIRAGANA
            0x3099,   // 3099..309A; INHERITED
            0x309B,   // 309B..309C; COMMON
            0x309D,   // 309D..309F; HIRAGANA
            0x30A0,   // 30A0..30A0; COMMON
            0x30A1,   // 30A1..30FA; KATAKANA
            0x30FB,   // 30FB..30FC; COMMON
            0x30FD,   // 30FD..3104; KATAKANA
            0x3105,   // 3105..3130; BOPOMOFO
            0x3131,   // 3131..318F; HANGUL
            0x3190,   // 3190..319F; COMMON
            0x31A0,   // 31A0..31BF; BOPOMOFO
            0x31C0,   // 31C0..31EF; COMMON
            0x31F0,   // 31F0..31FF; KATAKANA
            0x3200,   // 3200..321F; HANGUL
            0x3220,   // 3220..325F; COMMON
            0x3260,   // 3260..327E; HANGUL
            0x327F,   // 327F..32CF; COMMON
            0x32D0,   // 32D0..3357; KATAKANA
            0x3358,   // 3358..33FF; COMMON
            0x3400,   // 3400..4DBF; HAN
            0x4DC0,   // 4DC0..4DFF; COMMON
            0x4E00,   // 4E00..9FFF; HAN
            0xA000,   // A000..A4CF; YI
            0xA4D0,   // A4D0..A4FF; LISU
            0xA500,   // A500..A63F; VAI
            0xA640,   // A640..A69F; CYRILLIC
            0xA6A0,   // A6A0..A6FF; BAMUM
            0xA700,   // A700..A721; COMMON
            0xA722,   // A722..A787; LATIN
            0xA788,   // A788..A78A; COMMON
            0xA78B,   // A78B..A7FF; LATIN
            0xA800,   // A800..A82F; SYLOTI_NAGRI
            0xA830,   // A830..A83F; COMMON
            0xA840,   // A840..A87F; PHAGS_PA
            0xA880,   // A880..A8DF; SAURASHTRA
            0xA8E0,   // A8E0..A8FF; DEVANAGARI
            0xA900,   // A900..A92F; KAYAH_LI
            0xA930,   // A930..A95F; REJANG
            0xA960,   // A960..A97F; HANGUL
            0xA980,   // A980..A9FF; JAVANESE
            0xAA00,   // AA00..AA5F; CHAM
            0xAA60,   // AA60..AA7F; MYANMAR
            0xAA80,   // AA80..AADF; TAI_VIET
            0xAAE0,   // AAE0..AB00; MEETEI_MAYEK
            0xAB01,   // AB01..ABBF; ETHIOPIC
            0xABC0,   // ABC0..ABFF; MEETEI_MAYEK
            0xAC00,   // AC00..D7FB; HANGUL
            0xD7FC,   // D7FC..F8FF; UNKNOWN
            0xF900,   // F900..FAFF; HAN
            0xFB00,   // FB00..FB12; LATIN
            0xFB13,   // FB13..FB1C; ARMENIAN
            0xFB1D,   // FB1D..FB4F; HEBREW
            0xFB50,   // FB50..FD3D; ARABIC
            0xFD3E,   // FD3E..FD4F; COMMON
            0xFD50,   // FD50..FDFC; ARABIC
            0xFDFD,   // FDFD..FDFF; COMMON
            0xFE00,   // FE00..FE0F; INHERITED
            0xFE10,   // FE10..FE1F; COMMON
            0xFE20,   // FE20..FE2F; INHERITED
            0xFE30,   // FE30..FE6F; COMMON
            0xFE70,   // FE70..FEFE; ARABIC
            0xFEFF,   // FEFF..FF20; COMMON
            0xFF21,   // FF21..FF3A; LATIN
            0xFF3B,   // FF3B..FF40; COMMON
            0xFF41,   // FF41..FF5A; LATIN
            0xFF5B,   // FF5B..FF65; COMMON
            0xFF66,   // FF66..FF6F; KATAKANA
            0xFF70,   // FF70..FF70; COMMON
            0xFF71,   // FF71..FF9D; KATAKANA
            0xFF9E,   // FF9E..FF9F; COMMON
            0xFFA0,   // FFA0..FFDF; HANGUL
            0xFFE0,   // FFE0..FFFF; COMMON
            0x10000,  // 10000..100FF; LINEAR_B
            0x10100,  // 10100..1013F; COMMON
            0x10140,  // 10140..1018F; GREEK
            0x10190,  // 10190..101FC; COMMON
            0x101FD,  // 101FD..1027F; INHERITED
            0x10280,  // 10280..1029F; LYCIAN
            0x102A0,  // 102A0..102FF; CARIAN
            0x10300,  // 10300..1032F; OLD_ITALIC
            0x10330,  // 10330..1037F; GOTHIC
            0x10380,  // 10380..1039F; UGARITIC
            0x103A0,  // 103A0..103FF; OLD_PERSIAN
            0x10400,  // 10400..1044F; DESERET
            0x10450,  // 10450..1047F; SHAVIAN
            0x10480,  // 10480..107FF; OSMANYA
            0x10800,  // 10800..1083F; CYPRIOT
            0x10840,  // 10840..108FF; IMPERIAL_ARAMAIC
            0x10900,  // 10900..1091F; PHOENICIAN
            0x10920,  // 10920..1097F; LYDIAN
            0x10980,  // 10980..1099F; MEROITIC_HIEROGLYPHS
            0x109A0,  // 109A0..109FF; MEROITIC_CURSIVE
            0x10A00,  // 10A00..10A5F; KHAROSHTHI
            0x10A60,  // 10A60..10AFF; OLD_SOUTH_ARABIAN
            0x10B00,  // 10B00..10B3F; AVESTAN
            0x10B40,  // 10B40..10B5F; INSCRIPTIONAL_PARTHIAN
            0x10B60,  // 10B60..10BFF; INSCRIPTIONAL_PAHLAVI
            0x10C00,  // 10C00..10E5F; OLD_TURKIC
            0x10E60,  // 10E60..10FFF; ARABIC
            0x11000,  // 11000..1107F; BRAHMI
            0x11080,  // 11080..110CF; KAITHI
            0x110D0,  // 110D0..110FF; SORA_SOMPENG
            0x11100,  // 11100..1117F; CHAKMA
            0x11180,  // 11180..1167F; SHARADA
            0x11680,  // 11680..116CF; TAKRI
            0x12000,  // 12000..12FFF; CUNEIFORM
            0x13000,  // 13000..167FF; EGYPTIAN_HIEROGLYPHS
            0x16800,  // 16800..16A38; BAMUM
            0x16F00,  // 16F00..16F9F; MIAO
            0x1B000,  // 1B000..1B000; KATAKANA
            0x1B001,  // 1B001..1CFFF; HIRAGANA
            0x1D000,  // 1D000..1D166; COMMON
            0x1D167,  // 1D167..1D169; INHERITED
            0x1D16A,  // 1D16A..1D17A; COMMON
            0x1D17B,  // 1D17B..1D182; INHERITED
            0x1D183,  // 1D183..1D184; COMMON
            0x1D185,  // 1D185..1D18B; INHERITED
            0x1D18C,  // 1D18C..1D1A9; COMMON
            0x1D1AA,  // 1D1AA..1D1AD; INHERITED
            0x1D1AE,  // 1D1AE..1D1FF; COMMON
            0x1D200,  // 1D200..1D2FF; GREEK
            0x1D300,  // 1D300..1EDFF; COMMON
            0x1EE00,  // 1EE00..1EFFF; ARABIC
            0x1F000,  // 1F000..1F1FF; COMMON
            0x1F200,  // 1F200..1F200; HIRAGANA
            0x1F201,  // 1F210..1FFFF; COMMON
            0x20000,  // 20000..E0000; HAN
            0xE0001,  // E0001..E00FF; COMMON
            0xE0100,  // E0100..E01EF; INHERITED
            0xE01F0   // E01F0..10FFFF; UNKNOWN

        };

        private static final UnicodeScript[] scripts = {
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            BOPOMOFO,
            COMMON,
            INHERITED,
            GREEK,
            COMMON,
            GREEK,
            COMMON,
            GREEK,
            COMMON,
            GREEK,
            COMMON,
            GREEK,
            COPTIC,
            GREEK,
            CYRILLIC,
            INHERITED,
            CYRILLIC,
            ARMENIAN,
            COMMON,
            ARMENIAN,
            HEBREW,
            ARABIC,
            COMMON,
            ARABIC,
            COMMON,
            ARABIC,
            COMMON,
            ARABIC,
            COMMON,
            ARABIC,
            INHERITED,
            ARABIC,
            COMMON,
            ARABIC,
            INHERITED,
            ARABIC,
            COMMON,
            ARABIC,
            SYRIAC,
            ARABIC,
            THAANA,
            NKO,
            SAMARITAN,
            MANDAIC,
            ARABIC,
            DEVANAGARI,
            INHERITED,
            DEVANAGARI,
            COMMON,
            DEVANAGARI,
            BENGALI,
            GURMUKHI,
            GUJARATI,
            ORIYA,
            TAMIL,
            TELUGU,
            KANNADA,
            MALAYALAM,
            SINHALA,
            THAI,
            COMMON,
            THAI,
            LAO,
            TIBETAN,
            COMMON,
            TIBETAN,
            MYANMAR,
            GEORGIAN,
            COMMON,
            GEORGIAN,
            HANGUL,
            ETHIOPIC,
            CHEROKEE,
            CANADIAN_ABORIGINAL,
            OGHAM,
            RUNIC,
            COMMON,
            RUNIC,
            TAGALOG,
            HANUNOO,
            COMMON,
            BUHID,
            TAGBANWA,
            KHMER,
            MONGOLIAN,
            COMMON,
            MONGOLIAN,
            COMMON,
            MONGOLIAN,
            CANADIAN_ABORIGINAL,
            LIMBU,
            TAI_LE,
            NEW_TAI_LUE,
            KHMER,
            BUGINESE,
            TAI_THAM,
            BALINESE,
            SUNDANESE,
            BATAK,
            LEPCHA,
            OL_CHIKI,
            SUNDANESE,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            LATIN,
            GREEK,
            CYRILLIC,
            LATIN,
            GREEK,
            LATIN,
            GREEK,
            LATIN,
            CYRILLIC,
            LATIN,
            GREEK,
            INHERITED,
            LATIN,
            GREEK,
            COMMON,
            INHERITED,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            INHERITED,
            COMMON,
            GREEK,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            BRAILLE,
            COMMON,
            GLAGOLITIC,
            LATIN,
            COPTIC,
            GEORGIAN,
            TIFINAGH,
            ETHIOPIC,
            CYRILLIC,
            COMMON,
            HAN,
            COMMON,
            HAN,
            COMMON,
            HAN,
            COMMON,
            HAN,
            INHERITED,
            HANGUL,
            COMMON,
            HAN,
            COMMON,
            HIRAGANA,
            INHERITED,
            COMMON,
            HIRAGANA,
            COMMON,
            KATAKANA,
            COMMON,
            KATAKANA,
            BOPOMOFO,
            HANGUL,
            COMMON,
            BOPOMOFO,
            COMMON,
            KATAKANA,
            HANGUL,
            COMMON,
            HANGUL,
            COMMON,
            KATAKANA,
            COMMON,
            HAN,
            COMMON,
            HAN,
            YI,
            LISU,
            VAI,
            CYRILLIC,
            BAMUM,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            SYLOTI_NAGRI,
            COMMON,
            PHAGS_PA,
            SAURASHTRA,
            DEVANAGARI,
            KAYAH_LI,
            REJANG,
            HANGUL,
            JAVANESE,
            CHAM,
            MYANMAR,
            TAI_VIET,
            MEETEI_MAYEK,
            ETHIOPIC,
            MEETEI_MAYEK,
            HANGUL,
            UNKNOWN,
            HAN,
            LATIN,
            ARMENIAN,
            HEBREW,
            ARABIC,
            COMMON,
            ARABIC,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            ARABIC,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            KATAKANA,
            COMMON,
            KATAKANA,
            COMMON,
            HANGUL,
            COMMON,
            LINEAR_B,
            COMMON,
            GREEK,
            COMMON,
            INHERITED,
            LYCIAN,
            CARIAN,
            OLD_ITALIC,
            GOTHIC,
            UGARITIC,
            OLD_PERSIAN,
            DESERET,
            SHAVIAN,
            OSMANYA,
            CYPRIOT,
            IMPERIAL_ARAMAIC,
            PHOENICIAN,
            LYDIAN,
            MEROITIC_HIEROGLYPHS,
            MEROITIC_CURSIVE,
            KHAROSHTHI,
            OLD_SOUTH_ARABIAN,
            AVESTAN,
            INSCRIPTIONAL_PARTHIAN,
            INSCRIPTIONAL_PAHLAVI,
            OLD_TURKIC,
            ARABIC,
            BRAHMI,
            KAITHI,
            SORA_SOMPENG,
            CHAKMA,
            SHARADA,
            TAKRI,
            CUNEIFORM,
            EGYPTIAN_HIEROGLYPHS,
            BAMUM,
            MIAO,
            KATAKANA,
            HIRAGANA,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            GREEK,
            COMMON,
            ARABIC,
            COMMON,
            HIRAGANA,
            COMMON,
            HAN,
            COMMON,
            INHERITED,
            UNKNOWN
        };
        
        public static final String oneOfScript(java.lang.Character.UnicodeScript script) {
        	for (int i = 0; i < scripts.length; i++) {
        		if (scripts[i].name().equalsIgnoreCase(script.name())) {
        			int code = scriptStarts[i];
        			return new String(Character.toChars(code));
        		}
        	}
        	return "";
        }
    }

}
