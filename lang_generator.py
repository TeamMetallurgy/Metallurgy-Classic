import json

lines = {
    "block.metallurgyclassic.${ore}_ore_${stone}": "${ore} Ore",
    "block.metallurgyclassic.${metal}_bricks": "${metal} Bricks",
    "block.metallurgyclassic.${metal}_block": "${metal} Block",
    "item.metallurgyclassic.raw_${ore}": "Raw ${ore}",
    "item.metallurgyclassic.${metal}_ingot": "${metal} Ingot",
    "item.metallurgyclassic.${metal}_dust": "${metal} Dust",
    "item.metallurgyclassic.${metal}_tiny_dust": "Tiny ${metal} Dust",
    "item.metallurgyclassic.${metal}_nugget": "${metal} Nugget",
    "block.metallurgyclassic.${furnace}_furnace": "${furnace} Furnace",
    "block.metallurgyclassic.${chest}_chest": "${chest} Chest",
    "item.metallurgyclassic.raw_${utility}": "${utility}",
    "block.metallurgyclassic.${utility}_ore": "${utility} Ore",
    "metallurgyclassic.container.furnace.${furnace}": "${furnace} Furnace",
    "metallurgyclassic.container.chest.${chest}": "${chest} Chest",
    "item.metallurgyclassic.${tool}_axe": "${tool} Axe",
    "item.metallurgyclassic.${tool}_hoe": "${tool} Hoe",
    "item.metallurgyclassic.${tool}_pickaxe": "${tool} Pickaxe",
    "item.metallurgyclassic.${tool}_shovel": "${tool} Shovel",
    "item.metallurgyclassic.${tool}_sword": "${tool} Sword",
    "item.metallurgyclassic.${tool}_helmet": "${tool} Helmet",
    "item.metallurgyclassic.${tool}_chestplate": "${tool} Chestplate",
    "item.metallurgyclassic.${tool}_leggings": "${tool} Leggings",
    "item.metallurgyclassic.${tool}_boots": "${tool} Boots",
    "block.metallurgyclassic.${crusher}_crusher": "${crusher} Crusher",
    "metallurgyclassic.container.crusher.${crusher}": "${crusher} Crusher",
    "block.metallurgyclassic.${vanilla}_bricks": "${vanilla} Bricks",
    "item.metallurgyclassic.${vanilla}_dust": "${vanilla} Dust",
    "item.metallurgyclassic.${vanilla}_tiny_dust": "Tiny ${vanilla} Dust"
}
translation = {
    "item.metallurgyclassic.fertilizer": "Fertilizer",
    "item.metallurgyclassic.tar": "Tar",
    "item.metallurgyclassic.match": "Match",
    "item.metallurgyclassic.magnesium_igniter": "Magnesium Igniter",
    "block.metallurgyclassic.he_tnt": "HE TNT",
    "block.metallurgyclassic.le_tnt": "LE TNT"

}

stones = ['stone', 'granite', 'andesite', 'diorite', 'deepslate', 'netherrack', 'basalt', 'end_stone']
vanilla = ['iron', 'gold']
crushers = ['stone', 'copper', 'bronze', 'iron', 'steel']
furnaces = ['copper', 'bronze', 'iron', 'steel']
chests = ['brass', 'silver', 'gold', 'electrum', 'platinum']
utilities = ['sulfur', 'phosphorite', 'saltpeter', 'magnesium', 'bitumen', 'potash']
metals = ['copper', 'silver', 'platinum', 'ignatius', 'shadow_iron', 'midasium', 'vyroxeres', 'ceruclase', 'kalendrite', 'vulcanite', 'sanguinite', 'prometheum', 'deep_iron', 'oureclase', 'astral_silver', 'carmot', 'mithril', 'orichalcum', 'adamantine', 'atlarus', 'eximite']
catalysts = ['tin', 'manganese', 'zinc', 'lemurite', 'alduorite', 'infuscolium', 'rubracium', 'meutoite']
alloys = ['bronze', 'hepatizon', 'damascus_steel', 'angmallen', 'steel', 'brass', 'electrum', 'shadow_steel', 'inolashite', 'amordrine', 'black_steel', 'quicksilver', 'haderoth', 'celenegil', 'tartarite', 'desichalkos']

ores = metals + catalysts
items = metals + catalysts + alloys
tools = metals + alloys

templates = {
    "ore": ores,
    "metal": items,
    "tool": tools,
    "utility": utilities,
    "furnace": furnaces,
    "chest": chests,
    "crusher": crushers,
    "vanilla": vanilla,
}

def capitalize(string):
    strings = string.split("_")
    return " ".join([s.capitalize() for s in strings])

for item, value in lines.items():
    for template_key, template_values in templates.items():
        if template_key == 'ore':
            for stone_type in stones:
                key = "${" + template_key + "}"
                stone_key = "${stone}"
                if key in item:
                    for template_item in template_values:
                        translated_key = item.replace(key, template_item, 1)
                        translated_key = translated_key.replace(stone_key, stone_type, 1)
                        translated_string = value.replace(key, capitalize(template_item), 1)
                        translation[translated_key] = translated_string
        else:
            key = "${" + template_key + "}"
            if key in item:
                for template_item in template_values:
                    translated_key = item.replace(key, template_item, 1)
                    translated_string = value.replace(key, capitalize(template_item), 1)
                    translation[translated_key] = translated_string

print(json.dumps(translation, indent=2))