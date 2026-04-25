export function formatProductLabels(productLabelsMixEnList) {
    if (!productLabelsMixEnList || productLabelsMixEnList[0] === "NA") {
        return "";
    }

    const products = productLabelsMixEnList[0].split(",").map(p => p.trim());

    const formatted = products.map(product => {
        if (product.toLowerCase() === "mobile") {
            return `<phoneme alphabet="ipa" ph="ˈmoʊbaɪl">Mobile</phoneme>`;
        }
        return product;
    });

    return " with " + formatted.join(', <break time="200ms"/>');
}