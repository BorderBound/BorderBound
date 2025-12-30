import xml.etree.ElementTree as ET
import os

# List of input files
input_files = [
    "levelsEasy.xml",
    "levelsMedium.xml",
    "levelsHard.xml",
    "levelsCommunity.xml",
]

for input_file in input_files:
    # Parse XML
    tree = ET.parse(input_file)
    root = tree.getroot()

    # Renumber <level> elements sequentially
    for i, lvl in enumerate(root.findall("level")):
        lvl.set("number", str(i))

    # Prepare output file name (add _fixed before extension)
    base, ext = os.path.splitext(input_file)
    output_file = f"fixed_{base}{ext}"

    # Write back
    tree.write(output_file, encoding="utf-8", xml_declaration=True)
    print(f"Processed {input_file} -> {output_file}")
