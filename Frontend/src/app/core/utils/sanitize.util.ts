/**
 * Nettoie une saisie texte avant envoi API : trim, suppression des chevrons HTML, longueur bornée.
 * Ne remplace pas la validation serveur ; limite l’injection de balises côté client.
 */
export function sanitizePlainText(value: string | null | undefined, maxLength = 200): string {
  if (value == null) {
    return '';
  }
  return value
    .trim()
    .replace(/[<>]/g, '')
    .slice(0, maxLength);
}
