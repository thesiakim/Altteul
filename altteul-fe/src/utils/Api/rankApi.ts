import { rankApi } from "@utils/Api/commonApi";
import { RankApiFilter, RankingResponse } from "types/types";

export const getRank = async (filter: RankApiFilter): Promise<RankingResponse>  => {
    const res = await rankApi.get('', { params: filter })
    if (res.data.status === 200) {
        return res.data
    }
}