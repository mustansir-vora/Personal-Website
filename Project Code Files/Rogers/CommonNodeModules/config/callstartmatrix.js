import { Storage } from "@google-cloud/storage";
import * as logger from "../utils/logger.js";

const storage = new Storage();
const BUCKET_NAME = process.env.GCS_BUCKET_NAME;
const MATRIX_PATH = process.env.GCS_CALLSTART_MATRIX_FILE_NAME;

let cachedCallStartMatrix = null;
let callStartMatrixEtag = null;

async function getCallStartMatrix({ sessionId, tag }) {
    let status = null;
    let returnCode = null;
    let responsePayload = {};

    try {
        const file = storage.bucket(BUCKET_NAME).file(MATRIX_PATH);

        // ---- Metadata fetch 
        const metaStart = Date.now();
        const [metadata] = await file.getMetadata();
        const metaTime = Date.now() - metaStart;

        logger.logConsole(
            sessionId,
            tag,
            `CallStart matrix metadata fetched in ${metaTime} ms (etag=${metadata.etag})`
        );

        // serve from cache if ETag matches
        if (cachedCallStartMatrix && callStartMatrixEtag === metadata.etag) {
            logger.logConsole(
                sessionId,
                tag,
                `CallStart matrix served from cache (etag=${metadata.etag})`
            );
            status = 200;
            returnCode = "0";
            responsePayload = cachedCallStartMatrix;
        }
        // otherwise, download and cache 
        else {
            const downloadStart = Date.now();
            const [contents] = await file.download();
            const downloadTime = Date.now() - downloadStart;

            const matrix = JSON.parse(contents.toString());

            cachedCallStartMatrix = matrix;
            callStartMatrixEtag = metadata.etag;

            logger.logConsole(
                sessionId,
                tag,
                `CallStart matrix downloaded in ${downloadTime} ms (etag=${metadata.etag})`
            );

            status = 200;
            returnCode = "0";
            responsePayload = matrix;
        }
    } catch (err) {
        logger.logErrorResponse({ sessionId, tag, attemptCount: 1, err });

        console.log(JSON.stringify({
            event: "ErrorResponse",
            severity: "ERROR",
            sessionId,
            tag,
            details: JSON.stringify({
                message: `Failed to load call start matrix file at : ${BUCKET_NAME}/${MATRIX_PATH}`
            })
        }));

        status = 500;
        returnCode = "1";
    }

    return {
        Status: status,
        ReturnCode: returnCode,
        ResponsePayload: responsePayload
    };
}

export { getCallStartMatrix };