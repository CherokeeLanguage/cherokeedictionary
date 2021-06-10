<div style="display:table-row;padding-right:20px">
    <div style="display:table-cell">
        <div style="display:table-row">
            <div style="display:table-cell;padding-right:10px">
                <div style="display:table-row">
                    <div style="display:table-cell">${SyllabaryUtil.mixedTransliteration(name)}:</div>
                </div>
                if (showRedHelper) {
                <div style="display:table-row">
                    <div style="display:table-cell"><span style="color:red">${name}:</span></div>
                </div>
                }
            </div>

            <div style="display:table-cell">
                <div style="display:table-row">
                    <div style="display:table-cell">${SyllabaryUtil.mixedTransliteration(dialog)}</div>
                </div>
                if (showRedHelper) {
                <div style="display:table-row">
                    <div style="display:table-cell"><span style="color:red">${dialog}</span></div>
                </div>
                }

            </div>
        </div>
    </div>

    <div style="display:table-cell">
        <div style="display:table-row">
            <div style="display:table-cell;padding-right:10px">
                <div style="display:table-row">
                    <div style="display:table-cell">${engName}:</div>
                </div>
            </div>
            <div style="display:table-cell">
                <div style="display:table-row">
                    <div style="display:table-cell">${engDialog}</div>
                </div>
            </div>
        </div>
    </div>
</div>