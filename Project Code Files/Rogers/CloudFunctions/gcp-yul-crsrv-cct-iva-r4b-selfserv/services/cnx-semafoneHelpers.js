export function mergeConsumerSemafone(existingParams, consumerSemafoneDelta) {
	const existingApi = existingParams.api || {};
	const existingResponse = existingApi.response || {};
	const existingConsumerSemafone = existingResponse.consumerSemafone || {};
	return {
		api: {
			...existingApi,
			response: {
				...existingResponse,
				consumerSemafone: {
					...existingConsumerSemafone,
					...consumerSemafoneDelta
				}
			}
		}
	};
}

export function semafoneSessionQueryParams(params) {
	return {
		semafoneSessionId:
			params.api?.response?.consumerSemafone?.semafoneSessionId ?? "",
		semafoneCR: params.semafoneCR ?? ""
	};
}

export function semafoneFailureParam(failureKey) {
	return (params) => ({ ...params, [failureKey]: "systemError" });
}
