function formatDigitsSSML(number) {
  return number.split("").join("...");
}

export function DisambigPrompts(sortedAccounts, data, billingAccounts, flowLanguage) {

  const {
    accountNumberList,
    productLabelsMixEnList,
    productLabelsMixFrList,
    businessIndList
  } = data;

  let disambigMenuPrompt = "";
  let disambigMenuPromptNm1 = "";
  let disambigMenuPromptNm2 = "";
  let disambigMenuPromptNi1 = "";
  let disambigMenuPromptNi2 = "";

  //  EN Menu Option
  const menuAccountOption = ["say First. ", "say Second. ", "say Third. ", "say Fourth. "];
  const nmNiAccountOption = ["say First or press one.", " say Second or press two.", "say Third or press three.", "say Fourth or press four."]
  // Fr Menu Option
  const menuAccountOptionFr = ["premier. ", "deuxième. ", "troisième. ", "Quatrième. "];
  const nmNiAccountOptionFr = ["dites premier ou appuyez sur 1. ", "dites deuxième ou appuyez sur 2. ", "dites troisième ou appuyez sur 3. ", "dites quatrième ou appuyez sur 4. "];

  //Disambiguation Prompt Construction
  if (sortedAccounts.length === 1) {

    const last4 = accountNumberList[0]?.slice(-4);

    if (flowLanguage === "fr-CA") {

      const { products, category } = getProductAndCategory(
        0,
        flowLanguage,
        productLabelsMixEnList,
        productLabelsMixFrList,
        businessIndList
      );


      disambigMenuPrompt = `
  <speak>
    Appelez-vous au sujet du compte ${category} se terminant par...${formatDigitsSSML(last4)}. ${products}. 
  </speak>`;

      disambigMenuPromptNm1 = `
  <speak>
   Je suis désolé, je n'ai pas compris. Votre appel concerne-t-il le compte ${category} se terminant par...${formatDigitsSSML(last4)}. ${products}. Vous pouvez répondre par oui ou par non.
  </speak>`;

      disambigMenuPromptNm2 = `
  <speak>
    Si vous appelez au sujet du compte ${category} se terminant par...${formatDigitsSSML(last4)}. ${products}. ou dites non ou appuyez sur 2.
  </speak>`;

      disambigMenuPromptNi1 = `
  <speak>
    Je suis désolé, je n'ai pas entendu. Appelez-vous au sujet du compte ${category} se terminant par...${formatDigitsSSML(last4)}. ${products}. Vous pouvez répondre par oui ou par non.
  </speak>`;

      disambigMenuPromptNi2 = `
  <speak>
    Si vous appelez au sujet du compte ${category} se terminant par...${formatDigitsSSML(last4)}. ${products}. dites oui ou appuyez sur 1, ou dites non ou appuyez sur 2.
  </speak>`;

    } else {
      const { products, category } = getProductAndCategory(
        0,
        flowLanguage,
        productLabelsMixEnList,
        productLabelsMixFrList,
        businessIndList
      );

      disambigMenuPrompt = `<speak> Are you calling about the ${category} account ending in, <say-as interpret-as='telephone' google:style='zero-as-zero'>${last4}</say-as> ${products}. </speak>`;

      disambigMenuPromptNm1 = `
  <speak>
    I'm sorry, I didn't get that.
    Are you calling about the ${category} account ending in, 
    <say-as interpret-as='telephone' google:style='zero-as-zero'>${last4}</say-as> ${products}. You can say yes or no.
  </speak>`;

      disambigMenuPromptNm2 = `
  <speak>
    If you are calling about the ${category} account ending in,  
    <say-as interpret-as='telephone' google:style='zero-as-zero'>${last4}</say-as> ${products}. say yes or press 1, or say no or press 2.
  </speak>`;

      disambigMenuPromptNi1 = `
  <speak>
    I'm sorry, I couldn't hear that.
    Are you calling about the ${category} account ending in, 
    <say-as interpret-as='telephone' google:style='zero-as-zero'>${last4}</say-as> ${products}. You can say yes or no.
  </speak>`;

      disambigMenuPromptNi2 = `
  <speak>
    If you are calling about the ${category} account ending in, 
    <say-as interpret-as='telephone' google:style='zero-as-zero'>${last4}</say-as> ${products}. say yes or press 1, or say no or press 2.
  </speak>`;

    }
  } else {

    let menuBody = "";
    let menuBodyNm2 = "";
    let menuBodyNi2 = "";


    sortedAccounts.forEach((acc, index) => {
      const last4 = accountNumberList[index]?.slice(-4);

      if (flowLanguage === "fr-CA") {

        const { products, category } = getProductAndCategory(
          index,
          flowLanguage,
          productLabelsMixEnList,
          productLabelsMixFrList,
          businessIndList
        );


        const optionPrompt = menuAccountOptionFr[index];
        const dtmfOption = nmNiAccountOptionFr[index];

        menuBody += `Pour le compte ${category} se terminant par...${formatDigitsSSML(last4)}. ${products}. dites ${optionPrompt}`;

        menuBodyNm2 += `Pour le compte ${category} se terminant par...${formatDigitsSSML(last4)}. ${products}. ${dtmfOption}`;

        menuBodyNi2 += `Pour le compte ${category} se terminant par...${formatDigitsSSML(last4)}. ${products}. ${dtmfOption}`;
      }

      else {
        const { products, category } = getProductAndCategory(
          index,
          flowLanguage,
          productLabelsMixEnList,
          productLabelsMixFrList,
          businessIndList
        );


        const optionPrompt = menuAccountOption[index];
        const dtmfOption = nmNiAccountOption[index];

        menuBody += `
    For the ${category} account ending in, 
       <say-as interpret-as='telephone' google:style='zero-as-zero'>${last4}</say-as> ${products}. ${optionPrompt}`;

        menuBodyNm2 += `
    For the ${category} account ending in, 
      <say-as interpret-as='telephone' google:style='zero-as-zero'>${last4}</say-as> ${products}. ${dtmfOption}`;

        menuBodyNi2 += `
    For the ${category} account ending in, 
      <say-as interpret-as='telephone' google:style='zero-as-zero'>${last4}</say-as> ${products}. ${dtmfOption}`;
      }
    });

    if (flowLanguage === "fr-CA") {

      disambigMenuPrompt = `
  <speak>
    ${menuBody} ou dites, autre compte.
  </speak>`;

      disambigMenuPromptNm1 = `
  <speak>
    Je suis désolé, je n'ai pas compris.
    ${menuBody} Ou dites, autre compte.
  </speak>`;

      disambigMenuPromptNi1 = `
  <speak>
    Je suis désolé, je n'ai pas entendu.     
    ${menuBody} Ou dites, autre compte.
  </speak>`;

      disambigMenuPromptNm2 = `
  <speak>
    Je suis désolé, je n'ai toujours pas compris.
    ${menuBodyNm2} Ou dites autre compte. Ou appuyez sur ${billingAccounts + 1}.
  </speak>`;

      disambigMenuPromptNi2 = `
  <speak>
    Je suis désolé, je n'ai toujours pas entendu.
    ${menuBodyNi2} Ou dites autre compte. Ou appuyez sur  ${billingAccounts + 1}.
  </speak>`;

    } else {
      disambigMenuPrompt = `
  <speak>
    ${menuBody} or say a different account.
  </speak>`;

      disambigMenuPromptNm1 = `
  <speak>
    I'm sorry, I didn't get that.
    ${menuBody} Or say a different account.
  </speak>`;

      disambigMenuPromptNi1 = `
  <speak>
    I'm sorry, I couldn't hear that.
    ${menuBody} Or say a different account.
  </speak>`;

      disambigMenuPromptNm2 = `
  <speak>
    I'm sorry, I still didn't get that.
    ${menuBodyNm2} Or say a different account, or press ${billingAccounts + 1}.
  </speak>`;

      disambigMenuPromptNi2 = `
  <speak>
    I'm sorry, I still couldn't hear that.
    ${menuBodyNi2} Or say a different account, or press ${billingAccounts + 1}.
  </speak>`;
    }
  }


  return {
    disambigMenuPrompt,
    disambigMenuPromptNm1,
    disambigMenuPromptNm2,
    disambigMenuPromptNi1,
    disambigMenuPromptNi2
  };

}

function getProductAndCategory(
  index,
  flowLanguage,
  productLabelsMixEnList,
  productLabelsMixFrList,
  businessIndList
) {

  if (flowLanguage === "fr-CA") {
    return {
      products: formatProductLabels(productLabelsMixFrList[index], flowLanguage),
      category: businessIndList[index] ? "affaires" : "personnel"
    };
  }
  return {
    products: formatProductLabels(productLabelsMixEnList[index], flowLanguage),
    category: businessIndList[index] ? "Business" : "Personal"
  };
}

function formatProductLabels(productLabels, language) {
  if (!productLabels || productLabels === "NA") return "";

  const products = productLabels.split(",").map(p => p.trim());

  const phonemeMap = {
    mobile: `<phoneme alphabet="ipa" ph="ˈmoʊbaɪl">Mobile</phoneme>`
  };

  const formatted = products.map(p => {
    const key = p.toLowerCase();
    return language === "fr-CA" ? p : (phonemeMap[key] || p);
  });

  const config = {
    "fr-CA": { prefix: ' avec ', join: ' et ' },
    "default": { prefix: ' with ', join: ' and ' }
  };

  const { prefix, join } = config[language] || config["default"];
  // between pause
  const pause = `<break time="80ms"/>`;

  const head = formatted.slice(0, -1);
  const tail = formatted.slice(-1);

  const body = [
    head.join(`, ${pause}`),
    tail.length ? `${pause}${join}${tail[0]}` : ""
  ].join("").trim();

  return prefix + (head.length ? body : tail[0]);
}