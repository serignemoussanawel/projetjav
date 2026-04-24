from PIL import Image, ImageDraw, ImageFont


WIDTH = 1700
HEIGHT = 1100
BG = "white"
TEXT = "#0f172a"
LINE = "#475569"
BOX = "#e2e8f0"
HEADER = "#cbd5e1"
ACCENT = "#2563eb"


def load_font(size, bold=False):
    paths = []
    if bold:
        paths = [
            "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
            "/usr/share/fonts/truetype/liberation2/LiberationSans-Bold.ttf",
        ]
    else:
        paths = [
            "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
            "/usr/share/fonts/truetype/liberation2/LiberationSans-Regular.ttf",
        ]
    for path in paths:
        try:
            return ImageFont.truetype(path, size)
        except OSError:
            pass
    return ImageFont.load_default()


def centered(draw, xy, text, font, fill=TEXT):
    bbox = draw.textbbox((0, 0), text, font=font)
    x = xy[0] - (bbox[2] - bbox[0]) / 2
    y = xy[1] - (bbox[3] - bbox[1]) / 2
    draw.text((x, y), text, font=font, fill=fill)


def multiline(draw, x, y, lines, font, fill=TEXT, gap=6):
    current_y = y
    for line in lines:
        draw.text((x, current_y), line, font=font, fill=fill)
        bbox = draw.textbbox((x, current_y), line, font=font)
        current_y += (bbox[3] - bbox[1]) + gap


def draw_panel(draw, rect, title, lines, title_font, text_font):
    x1, y1, x2, y2 = rect
    draw.rounded_rectangle(rect, radius=20, fill=BOX, outline=LINE, width=3)
    draw.rounded_rectangle((x1, y1, x2, y1 + 56), radius=20, fill=HEADER, outline=LINE, width=3)
    draw.rectangle((x1, y1 + 28, x2, y1 + 56), fill=HEADER, outline=HEADER)
    draw.text((x1 + 22, y1 + 15), title, font=title_font, fill=TEXT)
    multiline(draw, x1 + 24, y1 + 82, lines, text_font)


def connect(draw, start, end, text=None, font=None):
    draw.line((start, end), fill=ACCENT, width=3)
    if text and font:
        mx = (start[0] + end[0]) / 2
        my = (start[1] + end[1]) / 2
        bbox = draw.textbbox((0, 0), text, font=font)
        pad = 6
        w = bbox[2] - bbox[0]
        h = bbox[3] - bbox[1]
        draw.rounded_rectangle((mx - w / 2 - pad, my - h / 2 - pad, mx + w / 2 + pad, my + h / 2 + pad),
                               radius=8, fill="white", outline=None)
        centered(draw, (mx, my), text, font, fill=TEXT)


def main():
    img = Image.new("RGBA", (WIDTH, HEIGHT), BG)
    draw = ImageDraw.Draw(img)

    title_font = load_font(28, bold=True)
    section_font = load_font(22, bold=True)
    text_font = load_font(18, bold=False)
    small_font = load_font(15, bold=False)

    centered(draw, (850, 40), "Schéma de structure du projet", title_font)

    panels = {
        "app": (650, 90, 1050, 220),
        "ui": (70, 300, 610, 730),
        "managers": (650, 300, 1050, 730),
        "models": (1090, 300, 1570, 730),
        "db": (650, 800, 1050, 1030),
    }

    draw_panel(draw, panels["app"], "Application",
               ["Main", "Point d'entrée JavaFX", "Initialise la base et ouvre l'écran de connexion"],
               section_font, text_font)

    draw_panel(draw, panels["ui"], "UI / Contrôleurs",
               [
                   "LoginController",
                   "AdminDashboardController",
                   "ChefBatimentDashboardController",
                   "EtudiantDashboardController",
                   "BatimentViewController",
                   "ChambreViewController",
                   "EtudiantViewController",
                   "UtilisateurViewController",
                   "StatisticsViewController",
               ], section_font, text_font)

    draw_panel(draw, panels["managers"], "Managers / Logique métier",
               [
                   "GestionUtilisateur",
                   "GestionBatiment",
                   "GestionChambre",
                   "GestionEtudiant",
                   "",
                   "Assurent les opérations CRUD",
                   "et les règles d'affectation",
               ], section_font, text_font)

    draw_panel(draw, panels["models"], "Modèles",
               [
                   "Utilisateur",
                   "Batiment",
                   "Chambre",
                   "Etudiant",
                   "UserRole",
                   "",
                   "Représentent les données métier",
               ], section_font, text_font)

    draw_panel(draw, panels["db"], "Accès aux données",
               [
                   "DatabaseManager",
                   "Connexion MySQL",
                   "Initialisation des tables",
                   "Méthodes bind* pour PreparedStatement",
                   "",
                   "Base: gestion_chambres",
               ], section_font, text_font)

    connect(draw, (850, 220), (340, 300), "ouvre l'interface", small_font)
    connect(draw, (850, 220), (850, 300), "initialise", small_font)
    connect(draw, (340, 730), (850, 730), "appelle", small_font)
    connect(draw, (1050, 515), (1090, 515), "manipule", small_font)
    connect(draw, (850, 730), (850, 800), "utilise", small_font)

    centered(draw, (1330, 780), "Relations métier", section_font)
    multiline(draw, 1110, 820,
              [
                  "Utilisateur -> UserRole",
                  "Utilisateur -> Batiment (batimentId)",
                  "Chambre -> Batiment (batimentId)",
                  "Chambre -> Etudiant (etudiantId)",
                  "Etudiant -> Chambre (chambreId)",
              ], text_font)

    img.save("schema_structure.png")


if __name__ == "__main__":
    main()
