import { Storage } from "@google-cloud/storage";
import csv from "csv-parser";
import * as logger from "../utils/logger.js";

const storage = new Storage();
const BUCKET_NAME = process.env.GCS_BUCKET_NAME;
const FILE_NAME = process.env.GCS_TERMINATION_MATRIX_FILE_NAME;

// Cache
let cachedTerminationMatrix = null;
let terminationMatrixEtag = null;

function filterColumn(rows, column, inputValue) {

    // If input is NA, match only *
    if (inputValue === "NA") {
        return rows.filter(r =>
            (r[column] ?? "").trim() === "*"
        );
    }

    // Exact match
    const exactMatches = rows.filter(r =>
        (r[column] ?? "").trim().toLowerCase() === inputValue.trim().toLowerCase()
    );

    // fallBack matches
    const fallBackMatches = rows.filter(r =>
        (r[column] ?? "").trim() === "*"
    );

    // Combine both matches`
    return [...exactMatches, ...fallBackMatches];
}

//  Resolve Final Row 
function resolveTerminationRow(sessionId, tag, rows, input) {

    let filtered = rows;

    const columnsInOrder = [
        "flagIndRuleId",
        "customerType",
        "language",
        "applicationId",
        "delinquencyRuleId",
        "feature",
        "intent",
        "intentGroup"
    ];

    for (const column of columnsInOrder) {
        filtered = filterColumn(filtered, column, input[column]);
        if (!filtered.length) break;
    }
    if (!filtered.length) {
        logger.logConsole(sessionId, tag, "No matching rows found after filtering.");
        return null;
    };
    logger.logConsole(sessionId, tag, `Total matched rows before sorting: ${filtered.length}`);
    // Sort DESC by priorityId
    filtered.sort((a, b) =>
        Number(b.priorityId) - Number(a.priorityId)
    );
    logger.logConsole(sessionId, tag, `After sorting DESC by priorityId → top priorityId: ${filtered[0].priorityId}`);
    return filtered[0];
}

async function getTerminationMatrix({ sessionId, tag, input }) {

    let status = null;
    let returnCode = null;
    let responsePayload = {};

    try {

        const file = storage.bucket(BUCKET_NAME).file(FILE_NAME);

        // ---- Fetch metadata (ETag) ----
        const metaStart = Date.now();
        const [metadata] = await file.getMetadata();
        const metaTime = Date.now() - metaStart;

        logger.logConsole(
            sessionId,
            tag,
            `Termination matrix metadata fetched in ${metaTime} ms(etag = ${metadata.etag})`
        );

        // ---- Serve from cache if unchanged ----
        if (cachedTerminationMatrix && terminationMatrixEtag === metadata.etag) {

            logger.logConsole(sessionId, tag, "Termination matrix served from cache");

        } else {

            const loadStart = Date.now();
            const rows = [];

            await new Promise((resolve, reject) => {
                file.createReadStream()
                    .pipe(csv())
                    .on("data", row => {
                        rows.push(row);
                    })
                    .on("end", resolve)
                    .on("error", reject);
            });

            cachedTerminationMatrix = rows;
            terminationMatrixEtag = metadata.etag;
            const loadTime = Date.now() - loadStart;

            logger.logConsole(
                sessionId,
                tag,
                `Termination matrix loaded in ${loadTime} ms(etag = ${metadata.etag})`
            );
        }

        // Apply Progressive Filtering
        const row = resolveTerminationRow(sessionId, tag, cachedTerminationMatrix, input);

        if (!row) {

            status = 200;
            returnCode = "1";
            responsePayload = {
                exitAction: "none",
                transferVQ: "NA",
                transferDID: "NA",
                terminationMessage: "NA"
            };

        } else {

            status = 200;
            returnCode = "0";
            responsePayload = {
                exitAction: row.exitAction || "none",
                transferVQ: row.transferVQ || "NA",
                transferDID: row.transferDID || "NA",
                terminationMessage: row.terminationMessage || "NA"
            };
        }

    } catch (err) {

        logger.logErrorResponse({ sessionId, tag, attemptCount: 1, err });

        status = 500;
        returnCode = "1";
        responsePayload = {
            exitAction: "none",
            transferVQ: "NA",
            transferDID: "NA",
            terminationMessage: "NA"
        };
    }

    return {
        Status: status,
        ReturnCode: returnCode,
        ResponsePayload: responsePayload
    };
}

export { getTerminationMatrix };
