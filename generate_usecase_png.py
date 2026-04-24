from PIL import Image, ImageDraw, ImageFont


WIDTH = 1600
HEIGHT = 980
BG = "white"
TEXT = "#111827"
LINE = "#475569"
DASH = "#64748b"
SYSTEM_FILL = "#f8fafc"
SYSTEM_STROKE = "#334155"
USECASE_STROKE = "#2563eb"


def load_font(size, bold=False):
    candidates = []
    if bold:
        candidates.extend(
            [
                "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
                "/usr/share/fonts/truetype/liberation2/LiberationSans-Bold.ttf",
            ]
        )
    else:
        candidates.extend(
            [
                "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
                "/usr/share/fonts/truetype/liberation2/LiberationSans-Regular.ttf",
            ]
        )

    for path in candidates:
        try:
            return ImageFont.truetype(path, size)
        except OSError:
            pass
    return ImageFont.load_default()


def centered_text(draw, xy, text, font, fill=TEXT):
    bbox = draw.textbbox((0, 0), text, font=font)
    x = xy[0] - (bbox[2] - bbox[0]) / 2
    y = xy[1] - (bbox[3] - bbox[1]) / 2
    draw.text((x, y), text, font=font, fill=fill)


def multiline_centered_text(draw, xy, lines, font, fill=TEXT, line_gap=6):
    sizes = [draw.textbbox((0, 0), line, font=font) for line in lines]
    heights = [bbox[3] - bbox[1] for bbox in sizes]
    total_height = sum(heights) + line_gap * (len(lines) - 1)
    y = xy[1] - total_height / 2
    for line, bbox, height in zip(lines, sizes, heights):
        width = bbox[2] - bbox[0]
        draw.text((xy[0] - width / 2, y), line, font=font, fill=fill)
        y += height + line_gap


def draw_actor(draw, x, y, label, font):
    draw.ellipse((x - 22, y, x + 22, y + 44), outline=TEXT, width=3)
    draw.line((x, y + 44, x, y + 107), fill=TEXT, width=3)
    draw.line((x - 35, y + 65, x + 35, y + 65), fill=TEXT, width=3)
    draw.line((x, y + 107, x - 30, y + 152), fill=TEXT, width=3)
    draw.line((x, y + 107, x + 30, y + 152), fill=TEXT, width=3)
    centered_text(draw, (x, y + 185), label, font)


def draw_usecase(draw, center, rx, ry, lines, font):
    x, y = center
    draw.ellipse((x - rx, y - ry, x + rx, y + ry), outline=USECASE_STROKE, width=2, fill="white")
    if isinstance(lines, str):
        lines = [lines]
    multiline_centered_text(draw, center, lines, font)


def draw_dashed_line(draw, start, end, dash_len=10, gap_len=8, width=2, fill=DASH):
    x1, y1 = start
    x2, y2 = end
    dx = x2 - x1
    dy = y2 - y1
    dist = (dx * dx + dy * dy) ** 0.5
    if dist == 0:
        return
    ux = dx / dist
    uy = dy / dist
    pos = 0
    while pos < dist:
        seg_end = min(pos + dash_len, dist)
        sx = x1 + ux * pos
        sy = y1 + uy * pos
        ex = x1 + ux * seg_end
        ey = y1 + uy * seg_end
        draw.line((sx, sy, ex, ey), fill=fill, width=width)
        pos += dash_len + gap_len


def main():
    img = Image.new("RGBA", (WIDTH, HEIGHT), BG)
    draw = ImageDraw.Draw(img)

    title_font = load_font(26, bold=True)
    label_font = load_font(18, bold=True)
    text_font = load_font(16, bold=False)
    small_font = load_font(15, bold=False)

    centered_text(draw, (800, 38), "Diagramme de cas d'utilisation", title_font)

    draw.rounded_rectangle((240, 80, 1360, 920), radius=18, outline=SYSTEM_STROKE, width=3, fill=SYSTEM_FILL)
    centered_text(draw, (800, 112), "Système de gestion des chambres du campus", label_font)

    draw_actor(draw, 125, 170, "Administrateur", label_font)
    draw_actor(draw, 115, 510, "Chef de bâtiment", label_font)
    draw_actor(draw, 1455, 360, "Étudiant", label_font)

    draw_usecase(draw, (470, 170), 120, 38, "Se connecter", text_font)
    draw_usecase(draw, (470, 270), 125, 38, "Se déconnecter", text_font)
    draw_usecase(draw, (470, 380), 140, 42, "Consulter tableau de bord", text_font)
    draw_usecase(draw, (760, 170), 110, 38, "Gérer bâtiments", text_font)
    draw_usecase(draw, (760, 270), 105, 38, "Gérer chambres", text_font)
    draw_usecase(draw, (760, 370), 105, 38, "Gérer étudiants", text_font)
    draw_usecase(draw, (760, 470), 112, 38, "Gérer utilisateurs", text_font)
    draw_usecase(draw, (760, 570), 125, 38, "Consulter statistiques", text_font)
    draw_usecase(draw, (760, 700), 165, 46, ["Gérer les chambres", "de son bâtiment"], text_font)
    draw_usecase(draw, (1080, 250), 115, 38, "Affecter une chambre", text_font)
    draw_usecase(draw, (1080, 360), 108, 38, "Libérer une chambre", text_font)
    draw_usecase(draw, (1080, 500), 150, 46, ["Consulter disponibilité", "des chambres"], text_font)
    draw_usecase(draw, (1080, 680), 105, 38, "Consulter son profil", text_font)
    draw_usecase(draw, (1080, 790), 110, 38, "Consulter sa chambre", text_font)

    solid_lines = [
        ((180, 220), (350, 170)),
        ((180, 235), (345, 270)),
        ((180, 255), (330, 380)),
        ((180, 280), (650, 170)),
        ((180, 300), (655, 270)),
        ((180, 320), (655, 370)),
        ((180, 340), (648, 470)),
        ((180, 360), (635, 570)),
        ((180, 380), (965, 250)),
        ((180, 395), (972, 360)),
        ((175, 560), (350, 170)),
        ((175, 580), (345, 270)),
        ((175, 600), (330, 380)),
        ((175, 620), (595, 700)),
        ((175, 640), (965, 250)),
        ((175, 660), (972, 360)),
        ((175, 680), (930, 500)),
        ((1400, 470), (1190, 680)),
        ((1400, 500), (1190, 790)),
        ((1400, 430), (590, 170)),
        ((1400, 450), (595, 270)),
    ]

    for start, end in solid_lines:
        draw.line((start, end), fill=LINE, width=2)

    dashed_lines = [
        ((865, 370), (965, 250), (900, 300)),
        ((865, 390), (972, 360), (900, 385)),
        ((865, 700), (930, 500), (865, 610)),
    ]

    for start, end, text_pos in dashed_lines:
        draw_dashed_line(draw, start, end)
        draw.text(text_pos, "<<include>>", font=small_font, fill=TEXT)

    img.save("diagramme_cas_utilisation.png")


if __name__ == "__main__":
    main()
