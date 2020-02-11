package com.def.team2.screen.rank

import com.def.team2.base.BaseRxPresenter
import com.def.team2.base.BaseRxView
import com.def.team2.network.Api
import com.def.team2.network.model.Idol
import com.def.team2.network.model.IdolGroup
import io.reactivex.Observable

interface RankContract {

    interface View : BaseRxView<Presenter> {
        fun getApiProvider(): Api
        fun setRank(data: List<IdolGroup>)
        fun updateVote(data: RankAdapter.Item)
    }

    interface Presenter : BaseRxPresenter {
        fun subscribeRank(ballotIds:String)
        fun subscribeVote(item:RankAdapter.Item)
    }

}